package lee.fund.web;

import com.google.common.base.Joiner;
import lee.fund.remote.app.server.RemoteApplication;

import java.util.Map;

/**
 * Created by noname on 15/10/16.
 */
public class WebApplication extends RemoteApplication {
    public WebApplication(Class<?> bootClass, String[] args) {
        super(bootClass, args, null);
    }

    @Override
    protected void beforeSetProperties(Map<String, Object> props) {
//        spring.thymeleaf.cache是否开启模板缓存，默认true
//        spring.thymeleaf.check-template-location是否检查模板路径是否存在，默认true
//        spring.thymeleaf.content-type指定Content-Type，默认为: text/html
//        spring.thymeleaf.enabled是否允许MVC使用Thymeleaf，默认为: true
//        spring.thymeleaf.encoding指定模板的编码，默认为: UTF-8
//        spring.thymeleaf.excluded-view-names指定不使用模板的视图名称，多个以逗号分隔.
//        spring.thymeleaf.mode指定模板的模式，具体查看StandardTemplateModeHandlers，默认为: HTML5
//        spring.thymeleaf.prefix指定模板的前缀，默认为:classpath:/templates/
//        spring.thymeleaf.suffix指定模板的后缀，默认为:.html
//        spring.thymeleaf.template-resolver-order指定模板的解析顺序，默认为第一个.
//        spring.thymeleaf.view-names指定使用模板的视图名，多个以逗号分隔.

        // velocity
        props.put("spring.velocity.content-type", "text/html");
        props.put("spring.velocity.resource-loader-path", "classpath:/view/");
        props.put("spring.velocity.request-context-attribute", "rc");
        props.put("spring.velocity.properties.layout-url", "layout/default.vm");
        props.put("spring.velocity.properties.input.encoding", "UTF-8");
        props.put("spring.velocity.properties.output.encoding", "UTF-8");
        //
        String[] directives = new String[]{
                "mtime.lark.web.velocity.directive.JsDirective",
                "mtime.lark.web.velocity.directive.CssDirective",
                "mtime.lark.web.velocity.directive.JsonDirective"
        };
        props.put("spring.velocity.properties.userdirective", Joiner.on(",").skipNulls().join(directives));
    }
}
