# 微信登录

相当于是经典登录的一个扩展，核心是向外提供一个URL服务

GET /wechatAuth?url=https://www.baidu.com

这个是用于浏览器跳转的请求，所以content必须符合一般浏览器和text/html规则。

最终浏览器会回到url指明的URL

## 结果

微信登录并非系统登录，微信登录只是允许系统获得微信身份，此时其他请求可能还是403.
但是这里将提供一个额外可访问路径
GEt /wechatUserDetail 获取当前用户微信信息


## 实现
可能需要走的路线:

1. GET /wechatAuth
   * 302 - https://wx...
1. GET https://wx... 
   * 302 - /otherAuth
1. GET /otherAuth
   * 302 - URL 


