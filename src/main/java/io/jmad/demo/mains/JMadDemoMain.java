package io.jmad.demo.mains;

import cern.accsoft.steering.jmad.domain.ex.JMadModelException;
import cern.accsoft.steering.jmad.domain.result.tfs.TfsSummary;
import cern.accsoft.steering.jmad.domain.var.enums.MadxGlobalVariable;
import cern.accsoft.steering.jmad.model.JMadModel;
import cern.accsoft.steering.jmad.modeldefs.domain.JMadModelDefinition;
import cern.accsoft.steering.jmad.service.JMadService;
import com.google.common.collect.Iterables;
import org.jmad.modelpack.domain.ModelPackage;
import org.jmad.modelpack.domain.ModelPackageVariant;
import org.jmad.modelpack.domain.ModelPackages;
import org.jmad.modelpack.domain.VariantType;
import org.jmad.modelpack.service.JMadModelPackageService;
import org.jmad.modelpack.service.conf.JMadModelPackageServiceStandaloneConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class JMadDemoMain {

    public static void main(String... args) throws JMadModelException {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(JMadModelPackageServiceStandaloneConfiguration.class);


        /*
         * The package service can be used to retrieve model definitions.
         */
        JMadModelPackageService modelPackageService = ctx.getBean(JMadModelPackageService.class);

        /*
         *  The jmad service is the topmost service to create jmad models
         */
        JMadService jmadService = ctx.getBean(JMadService.class);


        /*
         * Lets first of all see, which models we have available:
         */
        List<ModelPackageVariant> packageReleases = modelPackageService.availablePackages()
                .filter(v -> VariantType.RELEASE.equals(v.variant().type()))
                .collectList()
                .block();


        System.out.println("Released Model packages:");
        System.out.println("---");
        packageReleases.forEach(r -> System.out.println(r.fullName()));
        System.out.println("===\n");

        /*
         * Lets now pick one release:
         * a shortcut to this might come in handy at some point ;-)
         */
        ModelPackageVariant spsModelVariant = Iterables.getOnlyElement(
                packageReleases.stream()
                        .filter(v -> "jmad-modelpack-sps".equals(v.modelPackage().name()))
                        .filter(v -> "v2018.3".equals(v.variant().name()))
                        .collect(toList())
        );


        /**
         * Get the model definitions
         */
        List<JMadModelDefinition> spsModelDefinitions = modelPackageService.modelDefinitionsFrom(spsModelVariant)
                .collectList()
                .block();


        System.out.println("SPS model definitions:");
        System.out.println("---");
        spsModelDefinitions.forEach(md -> System.out.println(md.getName()));
        System.out.println("===\n");


        /**
         * Now we pick one:
         * (also here a shortcut would be great)
         */
        JMadModelDefinition cngsModelDefinition = Iterables.getOnlyElement(
                spsModelDefinitions.stream()
                        .filter(md -> "CNGS".equals(md.getName()))
                        .collect(toList())
        );


        JMadModel cngsModel = jmadService.createModel(cngsModelDefinition);
        cngsModel.init();

        TfsSummary tfsSummary = cngsModel.calcTwissSummary();
        System.out.println("Q1 of SPS: " + tfsSummary.getDoubleValue(MadxGlobalVariable.Q1));

        /* As we use spring here, we have to explicitely exit ;-)*/
        System.exit(0);
    }

}
