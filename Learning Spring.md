# 简介
Spring是一个开源的Java应用框架，它提供了一组全面而简单的工具，帮助开发人员在构建企业级应用程序时更加轻松和快速。Spring框架最初是为了解决企业Java应用开发的复杂性而设计的，它提供了一个模块化的架构，允许开发人员选择使用哪些组件，同时还能保证这些组件之间的无缝集成。
Spring的主要功能包括：

- **依赖注入（DI）**和**控制反转（IoC）**：通过这些功能，Spring可以帮助开发人员将业务逻辑和系统服务解耦，并使用轻量级的配置机制来配置应用程序。
- 模板化的编程模型：Spring提供了一系列模板类，可以帮助开发人员更方便地执行常见的任务，例如连接数据库、发送电子邮件、访问Web服务等。
- **AOP编程**：Spring提供了一种强大的面向切面编程（AOP）框架，可以帮助开发人员将业务逻辑和系统服务进行解耦。
- 与众多企业级技术集成：Spring可以与多种企业级技术集成，例如Java EE、Hibernate、Quartz和JMS
# 传统JavaWeb项目的问题与解决方案
## 业务功能耦合

- 问题：程序中手动new对象去实现功能，导致层与层之间相互嵌套。
- 解决：程序代码中不要手动new对象，第三方根据要求为程序**提供需要的Bean对象**。（IoC控制反转和DI依赖注入）
## 业务功能与扩展功能耦合

- 问题：**通用的事务功能**耦合在业务代码中**通用的日志功能**耦合在业务代码中。
- 解决：程序代码中不要手动new对象，第三方根据要求为程序提供需要的Bean对象的**代理对象**（AOP增强）。
# BeanFactory版本入门
## IoC - 控制反转
使用BeanFactory，利用beans.xml配置文件创建Bean，将Bean的创建权反转到Spring。
### 实例结构
```
└─src
    ├─main
    │  ├─java
    │  │  └─cqwu
    │  │      └─jwy
    │  │          │  UserService.java
    │  │          │
    │  │          ├─impl
    │  │          │      UserServiceImpl.java
    │  │          │
    │  │          └─test
    │  │                  BeanFactoryTest.java
    │  │
    │  └─resources
    │          beans.xml
    │
    └─test
        └─java
```
### 关键步骤

1. UserService接口
```java
package cqwu.jwy;

public interface UserService {
    String info();
}
```

2. UserServiceImpl类
```java
package cqwu.jwy.impl;

import cqwu.jwy.UserService;

public class UserServiceImpl implements UserService {
    @Override
    public String info() {
        return "UserService";
    }
}
```

3. 创建配置文件`src/main/resources/beans.xml`
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd">
    <!--配置UserServiceImpl-->
    <bean id="userService" class="cqwu.jwy.impl.UserServiceImpl"/>
</beans>
```

4. 测试
```java
package cqwu.jwy.test;

import cqwu.jwy.UserService;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;

public class BeanFactoryTest {
    public static void main(String[] args) {
        // 创建工厂对象
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        // 创建XML读取器，读取Bean的配置文件
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);
        // 读取配置文件给工厂
        reader.loadBeanDefinitions("beans.xml");
        // 根据id获取Bean实例对象
        UserService userService = (UserService) beanFactory.getBean("userService");
        // 验证
        System.out.println(userService.info());
    }
}
```
输出：
```bash
UserService
```
## DI - 依赖注入
使用BeanFactory，利用beans.xml配置文件创建Bean，并在配置文件中实现依赖注入。
### 实例结构
```
└─src
    └─main
        ├─java
        │  └─cqwu
        │      └─jwy
        │          │  UserDao.java
        │          │  UserService.java
        │          │
        │          ├─impl
        │          │      UserDaoImpl.java
        │          │      UserServiceImpl.java
        │          │
        │          └─test
        │                  BeanFactoryTest.java
        │
        └─resources
                beans.xml
```
### 关键步骤

1. UserServiceImpl类
```java
package cqwu.jwy.impl;

import cqwu.jwy.UserDao;
import cqwu.jwy.UserService;

public class UserServiceImpl implements UserService {
    private UserDao userDao;

    @Override
    public String info() {
        return "UserService";
    }

    /**
     * UserDao的Setter，由BeanFactory调用（DI）
     * @param userDao UserDao
     */
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
        System.out.println("BeanFactory调用了该方法并set了UserDao:" + userDao);
    }
}

```

2. 创建配置文件`src/main/resources/beans.xml`
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://www.springframework.org/schema/beans
  http://www.springframework.org/schema/beans/spring-beans.xsd">
  <!--配置UserServiceImpl-->
  <bean id="userService" class="cqwu.jwy.impl.UserServiceImpl">
    <!--name表示调用的方法名（删除set并首字母大写），ref表示注入userDao123-->
    <property name="userDao" ref="userDao123"/>
  </bean>
  <!--配置UserDaoImpl-->
  <bean id="userDao123" class="cqwu.jwy.impl.UserDaoImpl"/>
</beans>
```

3. 验证
```java
package cqwu.jwy.test;

import cqwu.jwy.UserService;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;

public class BeanFactoryTest {
    public static void main(String[] args) {
        // 创建工厂对象
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        // 创建XML读取器，读取Bean的配置文件
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);
        // 读取配置文件给工厂
        reader.loadBeanDefinitions("beans.xml");
        // 根据id获取Bean实例对象
        UserService userService = (UserService) beanFactory.getBean("userService");
        // 验证
        System.out.println(userService.info());
    }
}
```
输出：
```bash
BeanFactory调用了该方法并set了UserDao:cqwu.jwy.impl.UserDaoImpl@42d3bd8b
UserService
```

---

总结：
BeanFactory 快速入门
上面使用BeanFactory完成了loC思想的实现，下面去实现以下DI依赖注入：

1. 定义`UserDao`接口及其`UserDaolmpl`实现类；
2. 修改`UserServicelmpl`代码，添加一个`setUserDao(UserDao userDao)`用于接收注入的对象；
3. 修改`beans.xml`配置文件，在`UserDaolmpl`的`<bean>`中嵌入`<property>`配置注入；
4. 修改测试代码，获得`UserService`时 ，`setUserService`方法执行了注入操作。
