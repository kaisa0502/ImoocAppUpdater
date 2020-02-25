# 简介
 
慕课网Android应用内升级App(https://www.imooc.com/learn/1168)视频课程学习代码。
 
# 知识点总结
 
## 接口隔离原则
 
需要实现某个独立的功能时，我们可以通过接口去屏蔽具体的实现。比如下载 apk 可能使用不同的网络请求方式，我们可以将具体步骤写成接口，然后让其他开发者继承它就可以了。
 
## 版本适配
 
#### Android P ：允许Http请求
 
Android P的网络安全策略不允许访问 http 开头的 url 接口，需要在`res`文件夹下新建一个`xml`文件夹，然后添加一个配置文件，这里命名为`network_security_config`:
 
```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <base-config cleartextTrafficPermitted="true"></base-config>
</network-security-config>
```
 
然后在清单文件中添加：
 
```xml
<application
 
     android:networkSecurityConfig="@xml/network_security_config">
</application>
```
 
#### Android N ：FileProvider
 
[android file provider 完全解析](https://tianshimanbu.com/android-develop/android-file-provider.html)
 
在`xml`文件夹下
 
```xml
<?xml version="1.0" encoding="utf-8"?>
<paths>
    <root-path
        name="root"
        path="."></root-path>
    <files-path
        name="files"
        path="."></files-path>
    <!--
    文件映射在ContentProvider里面
    cachedir/targetFile -> content://cache/targetFile
    content://cache/targetFile -> cache-path/targetFile -> getCacheDir/targetFile
    -->
    <cache-path
        name="cache"
        path="."></cache-path>
 
    <external-path
        name="external"
        path="."></external-path>
 
    <external-cache-path
        name="external_cache"
        path="."></external-cache-path>
 
    <external-files-path
        name="external_file"
        path="."></external-files-path>
</paths>
```
 
#### Android O : 安装 Apk 权限
 
在清单文件中添加权限即可：
 
```xml
<uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES"
    tools:ignore="ProtectedPermissions" />
```
 
## MD5 校验文件
一般情况下，我们可以通过比对两个文件的 MD5 的值来判断这两个文件是不是同一个，这是因为文件的改动会引起md 5值的变动。将本地下载好的 apk 使用 MD5 比对，如果相同，说明存在相同文件，则不必重复下载。
 
> MD5即信息摘要算法，英文为 Message-Digest Algorithm。
 
## OKHttp 的使用
 
OKHttp 可以获取到队列中或者正在运行中的请求（Call），并可以取消它们。
 
## 网络请求中断的处理
 
网络请求过程应该考虑由于某些原因需要关闭请求。比如用户关闭了页面，页面销毁后控件已经不存在了，这时如果还继续请求并执行回调就可能出现空指针异常。所以有时需要手动取消（cancel）请求。
 
## 扩展与建议
 
- [ ] 断点续传功能：将文件分为多个区间，然后启用多个线程去下载，最后在合并为一个文件。
 
> 在 Http 的 Header 中有一个`range`属性，它可以指定下载一个文件的起始字节和终止字节，我们可以在请求时加上字节区间，让服务器返回指定的区间字节。然后在本地使用`RandomAccessFile`的`seek`方法指定写入字节的位置。
 
- [ ] 增量更新：
 
> 不必下载完整的新的apk，而是将旧的 apk 和新 apk 比对，把不同的部分生成一个patch，然后只需这个patch，和本地的 apk 做合并，生成一个新的 apk 来安装。这个会用到一个叫 **bsdiff**算法。
