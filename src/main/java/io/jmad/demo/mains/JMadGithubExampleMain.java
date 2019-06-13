package io.jmad.demo.mains;

import cern.accsoft.steering.jmad.domain.elem.Element;
import cern.accsoft.steering.jmad.domain.elem.JMadElementType;
import cern.accsoft.steering.jmad.domain.ex.JMadModelException;
import cern.accsoft.steering.jmad.model.JMadModel;
import cern.accsoft.steering.jmad.modeldefs.domain.JMadModelDefinition;
import cern.accsoft.steering.jmad.service.JMadService;
import cern.accsoft.steering.jmad.service.JMadServiceFactory;

import java.util.List;

public class JMadGithubExampleMain {

    public static void main(String ... args) throws JMadModelException {
        JMadService service = JMadServiceFactory.createJMadService();

        List<JMadModelDefinition> modelDefinitions = service.getModelDefinitionManager().getAllModelDefinitions();
        System.out.println(modelDefinitions);

        /* then find a model definition */
        JMadModelDefinition modelDefinition = service.getModelDefinitionManager()
                .getModelDefinition("example");

        /* create the model and initialize it */
        JMadModel model = service.createModel(modelDefinition);
        model.init();

        /* get all the elements */
        List<Element> elements = model.getActiveRange().getElements();

        /* print name and type of each element */
        for (Element element : elements) {
            System.out.println("name: " + element.getName() + "; type: " + JMadElementType.fromElement(element));
        }
    }
}
