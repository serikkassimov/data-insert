package kz.worldclass.finances.controlers;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Set;
import java.util.regex.Pattern;
import javax.persistence.Entity;
import javax.servlet.http.HttpServletRequest;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageController {
    public static final String ATTR_NAME_INCLUDED_VUES = "includedVues";
    
    @Autowired
    private HttpServletRequest request;
    
    @RequestMapping(value = "/")
    public String root() {
        URL pagesUrl;
        try {
            pagesUrl = PageController.class.getClassLoader()
                    .getResource("../../pages/index.jsp").toURI()
                    .resolve(".").normalize().toURL();
        } catch (URISyntaxException | MalformedURLException exception) {
            throw new RuntimeException(exception);
        }
        
        Reflections reflections = new Reflections(pagesUrl, new ResourcesScanner(), PageController.class.getClassLoader());
        Set<String> includedVues = reflections.getResources(Pattern.compile(".*\\.vue"));
        request.setAttribute(
                ATTR_NAME_INCLUDED_VUES,
                includedVues
        );
        
        return "index";
    }
}