package com.codingapi.springboot.leaf;

import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import java.util.*;

@Slf4j
public class AutoConfigurationImportSelector implements ImportBeanDefinitionRegistrar {

    private Set<Class<? extends LeafIdGenerate>> classes;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        String className = importingClassMetadata.getClassName();
        List<String> packageNames = new ArrayList<>();
        try {
            packageNames.add(Class.forName(className).getPackage().getName());

            Map<String,Object> annotations =  importingClassMetadata.getAnnotationAttributes(EnableLeaf.class.getName());
            assert annotations != null;
            String[] packages = (String[])annotations.get("scanBasePackages");
            packageNames.addAll(Arrays.asList(packages));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Reflections reflections =  new Reflections(new ConfigurationBuilder()
                .forPackages(packageNames.toArray(new String[]{}))
                .addScanners(Scanners.TypesAnnotated,Scanners.SubTypes));

        this.classes = reflections.getSubTypesOf(LeafIdGenerate.class);
        log.info("classes:{}",classes);

        LeafUtils.getInstance().setClasses(classes);

    }




}
