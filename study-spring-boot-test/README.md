# 注意事项
对于此项目，需要mvn install java-skill-learn之后，才能mvn install study-spring-boot-starter成功，
如果不行则则将study-spring-boot-autoconfigure模块以及study-spring-boot-starter
模块移除以下组件，直接指定版本。
```xml
    <parent>
        <artifactId>java-skill-learn</artifactId>
        <groupId>com.silvergravel</groupId>
        <version>1.0-SNAPSHOT</version>
    </parent>
```
## 链接

[掘金 SpringBoot自动配置](https://juejin.cn/post/7308915787570331684)