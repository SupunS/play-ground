package xml.tests.jaxb.partial;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "persons")
public class Persons {

    private String department;

    private String govern;

    private String name;

    private List<Person> personList = new ArrayList<Person>();

    @XmlElement(name = "person")
    public List<Person> getPersonList() {
        return personList;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGovern() {
        return govern;
    }

    public void setGovern(String govern) {
        this.govern = govern;
    }

    public void setPersonList(List<Person> personList) {
        this.personList = personList;
    }
}
