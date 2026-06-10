import CryptoJS from 'crypto-js'

function generateRandomString() {
  const characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789'
  let result = ''
  for (let i = 0; i < 32; i++) {
    result += characters.charAt(Math.floor(Math.random() * characters.length))
  }
  return result
}

export function generateAesKey() {
  return CryptoJS.enc.Utf8.parse(generateRandomString())
}

export function encryptBase64(str: CryptoJS.lib.WordArray) {
  return CryptoJS.enc.Base64.stringify(str)
}

export function decryptBase64(str: string) {
  return CryptoJS.enc.Base64.parse(str)
}

export function encryptWithAes(message: string, aesKey: CryptoJS.lib.WordArray) {
  return CryptoJS.AES.encrypt(message, aesKey, {
    mode: CryptoJS.mode.ECB,
    padding: CryptoJS.pad.Pkcs7,
  }).toString()
}

export function decryptWithAes(message: string, aesKey: CryptoJS.lib.WordArray) {
  return CryptoJS.AES.decrypt(message, aesKey, {
    mode: CryptoJS.mode.ECB,
    padding: CryptoJS.pad.Pkcs7,
  }).toString(CryptoJS.enc.Utf8)
}
