# Java�� Armeria�� �⺻���� �� ���� �����

https://engineering.linecorp.com/ko/blog/making-a-basic-server-with-java-armeria/ �� ���� ������.

## Armeria��?

https://engineering.linecorp.com/ko/blog/open-sourcing-armeria/

- Armeria�� Java 8 �� Netty �� �񵿱� RPC/API Ŭ���̾�Ʈ-������ ������ ��.
- LINE���� ���¼ҽ� ������Ʈ�� ������
- HTTP/2�� �Ĥ��� ���̾� �������ݷ� ����ϴ� ������ �񵿱� Thrift Ŭ���̾�Ʈ/������ �����ϱ� ���� ���� ������Ʈ(+ �⺻������ ���������� ������ ���� ������ Ȯ�强�� �پ�ٰ� ��.)
  - ex> HTTP/2�� ���� ���� ���� ���丮�� ó���ϴ� ���ÿ� Java EE �� ���ø����̼��� ������ �� �ִ�.

## �����غ���

### ������Ʈ �����
- Gradle, Java
- Gradle ���� �߰�
  - ������ �߰����ֱ�
```gradle
plugins {
    id 'java'
}

group 'org.example'
version '1.0-SNAPSHOT'

repositories {
    mavenCentral()
}

dependencies {
    testImplementation 'org.junit.jupiter:junit-jupiter-api:5.6.0'
    testRuntimeOnly 'org.junit.jupiter:junit-jupiter-engine'

    compile "com.linecorp.armeria:armeria:0.68.2"
    testCompile group: 'junit', name: 'junit', version: '4.12'
}

test {
    useJUnitPlatform()
}
```

### Hello Armeria...! ������ �����

src>main>java�� `ServerMain` Class ����

���⼭ import�Ҷ� `com.linecorp.armeria.~~~`�� import �ؾ� ��.

```java
import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.common.HttpStatus;
import com.linecorp.armeria.common.MediaType;
import com.linecorp.armeria.server.Server;
import com.linecorp.armeria.server.ServerBuilder;

import java.util.concurrent.CompletableFuture;

public class ServerMain {
    public static void main(String[] args){
       ServerBuilder sb = new ServerBuilder();
       sb.http(8080);  // �޼���� ��Ʈ�� ����

       // service() : �޼��带 ����ؼ� ������ ����
       sb.service("/hello",(ctx,res)->
                       HttpResponse.of(
                               HttpStatus.OK,
                               MediaType.HTML_UTF_8,
                               "<h1>Hello Armeria...!</h1>"));

       // �� �ΰ��� Ȱ���ؼ� Server �ν��Ͻ� ����
       Server server = sb.build();
       // start() �޼��带 ȣ��
       CompletableFuture<Void> future = server.start();
       future.join();
    }
}
```
- `http()` : port ����
- `service()` : ���� ����
- �� �� �޼��带 Ȱ���ؼ� Server �ν��Ͻ� ����
- `start()` : ȣ��

�� ��, �ڵ带 ������� �� `localhost:8080/hello`�� �����ϸ� �Ʒ��� ���� ���´�.

![hello_armeria](./readme_img/hello_armeria.JPG)

### �����

- src>main>java�� `CustomService` Class ����
- `HttpResponse.of()` �̿ܿ� ������̼����� ����� ���� ���� �߰���.

```java
import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.common.HttpStatus;
import com.linecorp.armeria.common.MediaType;
import com.linecorp.armeria.server.annotation.Get;
import com.linecorp.armeria.server.annotation.Param;

public class CustomService {
    @Get("/")
    public HttpResponse methodA(){
        return HttpResponse.of(
                HttpStatus.OK,
                MediaType.HTML_UTF_8,
                "<h1>Hello Custom Service...!</h1>");
    }

    @Get("/page/:number")
    public HttpResponse methodB(@Param("number") int number){
        return HttpResponse.of(
                HttpStatus.OK,
                MediaType.HTML_UTF_8,
                "<h1>Hello " + number + "...!</h1>");
    }
}
```
- `methodA()` : `GET "/"`�� �������� ȣ��
- `methodB()` : `GET "/page/<����>`�� �������� ȣ��

������̼� ����ؼ� ������� ������ �� �ִ� Ŭ������ ���� ��, `annotatedService()` �޼��� �Ű� ������ ���� Ŭ������ �ν��Ͻ��� ���޵ǵ��� �Ʒ�ó�� �ڵ� ����.

```java
import com.linecorp.armeria.server.Server;
import com.linecorp.armeria.server.ServerBuilder;
import java.util.concurrent.CompletableFuture;
  
public class ServerMain {
    public static void main(String[] args) {
        ServerBuilder sb = new ServerBuilder();
        sb.http(8080);
  
        sb.annotatedService(new CustomService());
  
        Server server = sb.build();
        CompletableFuture<Void> future = server.start();
        future.join();
    }
}
```

`ServerMain` �� �����ϰ�, `localhost:8080`���� �����ϸ� �Ʒ��� ���� ���´�.
![hello_custom_service](./readme_img/hello_service.JPG)

`localhost:8080/page/3`���� �����ϸ� �Ʒ��� ���� ���´�.
![hello_3](./readme_img/hello_3.JPG)