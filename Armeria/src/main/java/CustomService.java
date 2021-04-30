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
