/**
 * 需要手动使用的几个功能。
 * 导入方式:
 *
 * <code>
 * <pre>@Import(MiscSpringConfig::class)</pre>
 * </code>
 * 功能有:
 * <h4>GET /developmentHelpStatus</h4>
 * 如果有响应表示在开发或者演示平台。
 * <h4>POST /erase</h5>
 * 清理所有数据并且重启整个应用。
 * <h5>响应</h6>
 * <ul>
 *     <li>seconds(可通过common.seconds.toRestart定制)</li>
 *     <li>message</li>
 * </ul>
 * <h4>POST /passwordChanger</h4>
 * 修改当前登录用户密码。
 * <h5>请求</h5>
 * <ul>
 *     <li>originalPassword: 原密码</li>
 *     <li>password: 新密码</li>
 * </ul>
 *
 * @author CJ
 */
package me.jiangcai.common.ext.misc;