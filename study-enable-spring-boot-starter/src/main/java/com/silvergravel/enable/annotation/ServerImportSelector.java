package com.silvergravel.enable.annotation;

import com.silvergravel.enable.service.DnsServer;
import com.silvergravel.enable.service.HttpServer;
import com.silvergravel.enable.service.Server;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Map;

/**
 * @author DawnStar
 * @since : 2023/12/5
 */
public class ServerImportSelector implements ImportSelector {
    @Override
    @SuppressWarnings("all")
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        Map<String, Object> annotationAttributes = importingClassMetadata.getAnnotationAttributes(EnableServer.class.getName());
        Server.Type type = (Server.Type) annotationAttributes.get("type");
        return switch (type) {
            case HTTP -> new String[]{HttpServer.class.getName()};
            case DNS -> new String[]{DnsServer.class.getName()};
        };
    }
}
