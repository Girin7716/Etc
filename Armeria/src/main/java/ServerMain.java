import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.common.HttpStatus;
import com.linecorp.armeria.common.MediaType;
import com.linecorp.armeria.server.Server;
import com.linecorp.armeria.server.ServerBuilder;

import java.util.concurrent.CompletableFuture;

public class ServerMain {
    public static void main(String[] args){
        ServerBuilder sb = new ServerBuilder();
        sb.http(8080);

        sb.annotatedService(new CustomService());

        Server server = sb.build();
        CompletableFuture<Void> future = server.start();
        future.join();
//        ServerBuilder sb = new ServerBuilder();
//        sb.http(8080);  // 메서드로 포트를 지정
//
//        // service() : 메서드를 사용해서 서버를 구성
//        sb.service("/hello",(ctx,res)->
//                        HttpResponse.of(
//                                HttpStatus.OK,
//                                MediaType.HTML_UTF_8,
//                                "<h1>Hello Armeria...!</h1>"));
//
//        // 위 두개를 활용해서 Server 인스턴스 생성
//        Server server = sb.build();
//        // start() 메서드를 호출
//        CompletableFuture<Void> future = server.start();
//        future.join();
    }
}
