# 使用说明

### 1. 介绍

springboot项目中接口返回Vo或接受参数的From中的字段进行脱敏与加解密，该功能生效限于http请求体中数据
（@RequestBody、@ResponseBody）,脱敏及加解密可进行扩展，目前只提供了基本的脱敏、加解密功能，后续会完善。

### 2. 使用方式

maven pom引入
 <dependency>
  <groupId>com.security</groupId>
  <artifactId>sensitive-spring-boot-starter</artifactId>
  <version>0.0.1-SNAPSHOT</version>
 </dependency>
启动类上添加@EnableSecurity
该注解上有三个参数，分别为是sensitive否开启脱敏功能、security是否开启加密功能及针对脱敏与加解密类所在的包名，包可不指定，默认为添加@EnableSecurity注解类所在的包的顶级包名+子包名

```
 @SpringBootApplication
 @EnableSensitive(security = true, sensitive = true, packages = "com.example")
 public class LeanApplication {
	public static void main(String[] args) {
     ConfigurableApplicationContext run = SpringApplication.run(LeanApplication.class, 			args);
	 }
 }
```



在需要的接口上添加@Security注解，对与需要脱敏的字段添加@Sensitive注解，需要加密的字段添加 @Security注解

  application.yml

```yaml
  spring:
    sensitive:
      security:
        type: AES   #加密方式，默认AES
        mode: HEX   #加密后的字节数组转为字符串方式：HEX 16进制，BASE64 base64
        secret: +6cuvzvyrFZpRG9pf3r7eQ==    #加密密钥
        charset: UTF-8  
        maxDeep: 10     #处理脱敏、加解密对象的最大深度，防止相互依赖导致递归栈内存溢出，默认10
```



    @Data
    public class Message {
      
        private Integer id;
      
        @Sensitive(type = SensitiveTypeEnum.CHINESE_NAME)
        private String name;
      
        @Sensitive(type = SensitiveTypeEnum.ID_CARD)
        private String idCard;
      
        @Sensitive(type = SensitiveTypeEnum.EMAIL)
        private String email;
      
        @Security
        private String phone;
      
        private List<Company> companyList;
    }
    
    @Data
    public class Company {
    
        private Integer id;
    
        @Sensitive(type = SensitiveTypeEnum.CHINESE_NAME)
        private String name;
    
        @Sensitive(type = SensitiveTypeEnum.ID_CARD)
        private String code;
    }
      
    @RestController
    public class SecurityController {
    
        @GetMapping("/test")
        @Security
        public Message test() {
            Message message = new Message();
            message.setId(1);
            message.setName("杨哥");
            message.setIdCard("610502199323223323");
            message.setEmail("907746999@qq.com");
            message.setPhone("18712346789");
            List<Company> companyList = new ArrayList<>();
            for (int i = 0; i < 2; i++) {
                Company company = new Company();
                company.setId(i);
                company.setCode("NO212121212112" + i);
                company.setName("公司" + i);
                companyList.add(company);
            }
            message.setCompanyList(companyList);
            return message;
        }
    
        @PostMapping("/test")
        public Message test(@Security @RequestBody Message message) {
            return message;
        }
    }

  效果：
  GET方式请求test接口

![image](https://github.com/coder-yangge/sensitive-spring-boot-starter/blob/master/image/test-get.png)

  Post方式请求test接口

![image](https://github.com/coder-yangge/sensitive-spring-boot-starter/blob/master/image/test-post.png)

### 3. 扩展

TODO 

