# fengyang-Shopping
风漾

启动nginx

```sh
nginx
# 修改配置文件
/opt/nginx/conf
vim nginx.conf
# 修改配置文件后 加载
nginx -s reload
# 关闭
nginx -s stop
```

启动fdfs

```sh
service fdfs_trackerd start
service fdfs_storaged start
```

启动elasticsearch

```sh
# 出于安全考虑，elasticsearch默认不允许以root账号运行。
# 切换到fengyang用户
su - fengyang 
/home/fengyang/elasticsearch/bin
./elasticsearch
bin/elasticsearch -d(后台运行)
```

ubuntu启动kibana

```sh
/opt/kibana-6.2.4/bin
./kibana
```

启动rabbitmq

```sh
# 启动docker
service docker start
# 启动已存在的rabbitmq容器
docker start sad_kilby
# http://192.168.56.110:15672
fengyang/fengyang
```

启动redis

```sh
cd /home/fengyang/redis-4.0.9/
redis-server redis.conf
redis-cli 客户端控制台，包含参数：
-h xxx 指定服务端地址，缺省值是127.0.0.1
-p xxx 指定服务端端口，缺省值是6379
```



**前端**

启动后台管理界面

```shell
npm run dev
```

启动门户界面

```sh
live-server --port=9002
```



**后台**

启动注册中心FyRegistry

启动网关FyGateway

学习elasticsearch时，只需启动搜索服务FySearchApplication和商品服务FyItemApplication，和门户界面。

学习rabbitmq时，需启动商品服务，搜索服务，商品详情。



启动商品服务FyItemApplication

启动搜索服务FySearchApplication

启动商品详情服务FyPageApplication （页面静态化）



启动用户服务（用户注册，登录）和短信服务（用户注册）需启动redis。

