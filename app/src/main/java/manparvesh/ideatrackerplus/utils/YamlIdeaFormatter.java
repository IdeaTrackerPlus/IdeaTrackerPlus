package manparvesh.ideatrackerplus.utils;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.introspector.BeanAccess;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.introspector.PropertyUtils;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import java.io.Reader;
import java.io.Writer;
import java.text.ParseException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import manparvesh.ideatrackerplus.models.Idea;
import manparvesh.ideatrackerplus.models.Ideas;

public class YamlIdeaFormatter implements IdeaFormatter {

    private final Yaml yaml;

    public YamlIdeaFormatter() {
        yaml = configureYaml();
    }

    @Override
    public void serialize(List<Idea> ideas, Writer writer) {
        yaml.dump(new Ideas(ideas), writer);
    }

    @Override
    public String serialize(List<Idea> ideas) {
        return yaml.dump(new Ideas(ideas));
    }

    @Override
    public List<Idea> deserialize(Reader reader) throws ParseException{
        try {
            Ideas ideas = yaml.loadAs(reader, Ideas.class);
            return ideas.ideas;
        } catch (YAMLException ex) {
            throw new ParseException("File does not contain valid YAML", 0);
        }
    }

    private Yaml configureYaml() {

        Constructor constructor = new Constructor(Ideas.class);

        Representer repr = new MyRepresenter();
        repr.setPropertyUtils(new UnsortedPropertyUtils());

        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);

        return new Yaml(constructor, repr, options);
    }

    private static class UnsortedPropertyUtils extends PropertyUtils {
        @Override
        protected Set<Property> createPropertySet(Class<?> type, BeanAccess bAccess) {
            // preserve order of properties
            return new LinkedHashSet<>(getPropertiesMap(type, BeanAccess.FIELD).values());
        }
    }

    private static class MyRepresenter extends Representer {
        @Override
        protected MappingNode representJavaBean(Set<Property> properties, Object javaBean) {
            // hide bean type in yaml
            if (!classTags.containsKey(javaBean.getClass())) {
                addClassTag(javaBean.getClass(), Tag.MAP);
            }
            return super.representJavaBean(properties, javaBean);
        }
    }

}
