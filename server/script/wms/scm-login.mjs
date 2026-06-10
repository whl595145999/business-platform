#!/usr/bin/env node
/**
 * 与 admin-web 一致的加密登录，输出 access_token（仅 stdout，供 shell 脚本捕获）。
 *
 * 环境变量:
 *   BASE_URL, USERNAME, PASSWORD, TENANT_ID, CLIENT_ID
 *   RSA_PUBLIC_KEY, RSA_PRIVATE_KEY  （默认与 admin-web .env 一致）
 *   CAPTCHA_CODE, CAPTCHA_UUID       （手动传验证码时）
 *   REDIS_CLI                        （默认 redis-cli，用于 dev 自动读 math 验证码答案）
 *   ADMIN_WEB_DIR                    （crypto-js / jsencrypt 所在项目，默认 ../../../business-platform-admin-web）
 */
import { createRequire } from 'node:module';
import { spawnSync } from 'node:child_process';
import { dirname, resolve } from 'node:path';
import { fileURLToPath } from 'node:url';

const __dirname = dirname(fileURLToPath(import.meta.url));

const BASE_URL = (process.env.BASE_URL || 'http://localhost:8080').replace(/\/$/, '');
const USERNAME = process.env.USERNAME || 'admin';
const PASSWORD = process.env.PASSWORD || 'admin123';
const TENANT_ID = process.env.TENANT_ID || '000000';
const CLIENT_ID = process.env.CLIENT_ID || 'e5cd7e4891bf95d1d19206ce24a7b32e';
const RSA_PUBLIC_KEY =
  process.env.RSA_PUBLIC_KEY ||
  'MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAKoR8mX0rGKLqzcWmOzbfj64K8ZIgOdHnzkXSOVOZbFu/TJhZ7rFAN+eaGkl3C4buccQd/EjEsj9ir7ijT7h96MCAwEAAQ==';
const RSA_PRIVATE_KEY =
  process.env.RSA_PRIVATE_KEY ||
  'MIIBVAIBADANBgkqhkiG9w0BAQEFAASCAT4wggE6AgEAAkEAmc3CuPiGL/LcIIm7zryCEIbl1SPzBkr75E2VMtxegyZ1lYRD+7TZGAPkvIsBcaMs6Nsy0L78n2qh+lIZMpLH8wIDAQABAkEAk82Mhz0tlv6IVCyIcw/s3f0E+WLmtPFyR9/WtV3Y5aaejUkU60JpX4m5xNR2VaqOLTZAYjW8Wy0aXr3zYIhhQQIhAMfqR9oFdYw1J9SsNc+CrhugAvKTi0+BF6VoL6psWhvbAiEAxPPNTmrkmrXwdm/pQQu3UOQmc2vCZ5tiKpW10CgJi8kCIFGkL6utxw93Ncj4exE/gPLvKcT+1Emnoox+O9kRXss5AiAMtYLJDaLEzPrAWcZeeSgSIzbL+ecokmFKSDDcRske6QIgSMkHedwND1olF8vlKsJUGK3BcdtM8w4Xq7BpSBwsloE=';
const REDIS_CLI = process.env.REDIS_CLI || 'redis-cli';

const adminWebDir = resolve(
  process.env.ADMIN_WEB_DIR || resolve(__dirname, '../../../business-platform-admin-web')
);
const require = createRequire(resolve(adminWebDir, 'package.json'));
const CryptoJS = require('crypto-js');
const JSEncrypt = require('jsencrypt');

const ENCRYPT_HEADER = 'encrypt-key';

function fail(msg) {
  console.error(msg);
  process.exit(1);
}

function generateRandomString() {
  const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
  let result = '';
  for (let i = 0; i < 32; i++) {
    result += chars.charAt(Math.floor(Math.random() * chars.length));
  }
  return result;
}

function generateAesKey() {
  return CryptoJS.enc.Utf8.parse(generateRandomString());
}

function rsaEncrypt(txt) {
  const encryptor = new JSEncrypt();
  encryptor.setPublicKey(RSA_PUBLIC_KEY);
  const encrypted = encryptor.encrypt(txt);
  if (!encrypted) {
    fail('RSA encrypt failed — check RSA_PUBLIC_KEY');
  }
  return encrypted;
}

function rsaDecrypt(txt) {
  const encryptor = new JSEncrypt();
  encryptor.setPrivateKey(RSA_PRIVATE_KEY);
  return encryptor.decrypt(txt);
}

function encryptRequestBody(payload) {
  const aesKey = generateAesKey();
  const headerValue = rsaEncrypt(CryptoJS.enc.Base64.stringify(aesKey));
  const body = CryptoJS.AES.encrypt(JSON.stringify(payload), aesKey, {
    mode: CryptoJS.mode.ECB,
    padding: CryptoJS.pad.Pkcs7
  }).toString();
  return { headerValue, body };
}

function decryptResponseBody(encryptedBody, headerValue) {
  const base64Str = rsaDecrypt(headerValue);
  if (!base64Str) {
    fail('RSA decrypt response header failed');
  }
  const aesKey = CryptoJS.enc.Base64.parse(base64Str);
  const plain = CryptoJS.AES.decrypt(encryptedBody, aesKey, {
    mode: CryptoJS.mode.ECB,
    padding: CryptoJS.pad.Pkcs7
  }).toString(CryptoJS.enc.Utf8);
  return JSON.parse(plain);
}

function readCaptchaFromRedis(uuid) {
  const key = `global:captcha_codes:${uuid}`;
  const result = spawnSync(REDIS_CLI, ['GET', key], { encoding: 'utf8' });
  if (result.status !== 0 || !result.stdout?.trim()) {
    return '';
  }
  return result.stdout.trim().replace(/^"|"$/g, '');
}

async function fetchCaptcha() {
  const res = await fetch(`${BASE_URL}/auth/code`, { method: 'GET' });
  const text = await res.text();
  let doc;
  try {
    doc = JSON.parse(text);
  } catch {
    fail(`GET /auth/code invalid JSON: ${text.slice(0, 200)}`);
  }
  if (doc.code !== 200) {
    fail(`GET /auth/code failed: code=${doc.code}, msg=${doc.msg}`);
  }
  return doc.data || {};
}

async function login() {
  let code = process.env.CAPTCHA_CODE || '';
  let uuid = process.env.CAPTCHA_UUID || '';

  const captcha = await fetchCaptcha();
  if (captcha.captchaEnabled === false) {
    code = '';
    uuid = '';
  } else {
    uuid = uuid || captcha.uuid || '';
    if (!code && uuid) {
      code = readCaptchaFromRedis(uuid);
    }
    if (!code || !uuid) {
      fail(
        '验证码未解析。可选方案:\n' +
          '  1) TOKEN=<access_token> 跳过登录（浏览器 Network 复制）\n' +
          '  2) CAPTCHA_CODE=答案 CAPTCHA_UUID=uuid 手动传入\n' +
          '  3) 本机 redis-cli 可读 global:captcha_codes:<uuid>（dev math 验证码）\n' +
          '  4) 临时 --captcha.enable=false'
      );
    }
  }

  const payload = {
    clientId: CLIENT_ID,
    grantType: 'password',
    tenantId: TENANT_ID,
    username: USERNAME,
    password: PASSWORD,
    code,
    uuid
  };

  const { headerValue, body } = encryptRequestBody(payload);
  const res = await fetch(`${BASE_URL}/auth/login`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      [ENCRYPT_HEADER]: headerValue
    },
    body
  });

  const respHeader = res.headers.get(ENCRYPT_HEADER);
  const respText = await res.text();
  let doc;
  if (respHeader) {
    doc = decryptResponseBody(respText, respHeader);
  } else {
    try {
      doc = JSON.parse(respText);
    } catch {
      fail(`POST /auth/login invalid JSON: ${respText.slice(0, 200)}`);
    }
  }

  if (doc.code !== 200) {
    fail(`login failed: code=${doc.code}, msg=${doc.msg}`);
  }

  const token = doc.data?.access_token;
  if (!token) {
    fail('login ok but access_token missing in response');
  }
  process.stdout.write(token);
}

login().catch((err) => fail(String(err?.stack || err)));
