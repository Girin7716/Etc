# Java와 Armeria로 기본적인 웹 서버 만들기

https://engineering.linecorp.com/ko/blog/making-a-basic-server-with-java-armeria/ 를 보고 따라함.

## Armeria란?

https://engineering.linecorp.com/ko/blog/open-sourcing-armeria/

- Armeria는 Java 8 및 Netty 상에 비동기 RPC/API 클라이언트-서버를 구현한 것.
- LINE에서 오픈소스 프로젝트로 배포함
- HTTP/2를 ㅔㅅ션 레이어 프로토콜로 사용하는 고성능의 비동기 Thrift 클라이언트/서버를 구축하기 위해 만든 프로젝트(+ 기본적으로 프로토콜의 제약을 받지 않으며 확장성이 뛰어나다고 함.)
  - ex> HTTP/2를 통해 정적 파일 디렉토리를 처리하는 동시에 Java EE 웹 애플리케이션을 실행할 수 있다.

## 따라해보기

### 프로젝트 만들기
- Gradle, Java
- Gradle 설정 추가
  - 의존성 추가해주기
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

### Hello Armeria...! 페이지 만들기

src>main>java에 `ServerMain` Class 생성

여기서 import할때 `com.linecorp.armeria.~~~`로 import 해야 됨.

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
       sb.http(8080);  // 메서드로 포트를 지정

       // service() : 메서드를 사용해서 서버를 구성
       sb.service("/hello",(ctx,res)->
                       HttpResponse.of(
                               HttpStatus.OK,
                               MediaType.HTML_UTF_8,
                               "<h1>Hello Armeria...!</h1>"));

       // 위 두개를 활용해서 Server 인스턴스 생성
       Server server = sb.build();
       // start() 메서드를 호출
       CompletableFuture<Void> future = server.start();
       future.join();
    }
}
```
- `http()` : port 지정
- `service()` : 서버 구성
- 위 두 메서드를 활용해서 Server 인스턴스 생성
- `start()` : 호출

이 후, 코드를 실행시켠 뒤 `localhost:8080/hello`에 접속하면 아래와 같이 나온다.

![hello_armeria](./readme_img/hello_armeria.JPG)

### 라우팅

- src>main>java에 `CustomService` Class 생성
- `HttpResponse.of()` 이외에 어노테이션으로 라우팅 관련 설정 추가함.

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
- `methodA()` : `GET "/"`로 들어왔을때 호출
- `methodB()` : `GET "/page/<숫자>`로 들어왔을때 호출

어노테이션 사용해서 라우팅을 적용할 수 있는 클래스를 만든 뒤, `annotatedService()` 메서드 매개 변수를 통해 클래스의 인스턴스가 전달되도록 아래처럼 코드 수정.

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

`ServerMain` 을 실행하고, `localhost:8080`으로 접속하면 아래와 같이 나온다.
![hello_custom_service](./readme_img/hello_service.JPG)

`localhost:8080/page/3`으로 접속하면 아래와 같이 나온다.
![hello_3](./readme_img/hello_3.JPG)