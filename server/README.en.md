主脚架：RuoYi-Vue-Plus
后台前端：plus-ui
商城端：mall4uni / Mall4j 参考改造  https://gitee.com/gz-yami/mall4uni?utm_source=chatgpt.com
PDA端：自建 uni-app，参考 JeeWMS 流程  https://github.com/yusiwen/jeewms?utm_source=chatgpt.com



business-platform

├── apps

│   ├── admin-web        # plus-ui

│   ├── uniapp-client    # 商城端

│   ├── pda-client       # PDA端

│   ├── openapi-server

│   ├── scheduler-job

│   └── gateway-server

│

├── framework            # RuoYi-Vue-Plus 公共框架能力

├── infrastructure       # 系统、认证、租户、文件、消息

├── business             # 商品、订单、库存、WMS、OMS、CMS、财务、会员

└── deploy


镜像配置

{

"builder": {

    "gc": {

      "defaultKeepStorage": "20GB",

      "enabled": true

    }

},

"experimental": false,

"registry-mirrors": [

    "https://docker.1ms.run",

    "https://hub.rat.dev",

    "https://dockerproxy.com",

    "https://docker.m.daocloud.io"

]

}


ALTER USER 'root'@'localhost' IDENTIFIED BY 'root';

FLUSH PRIVILEGES;