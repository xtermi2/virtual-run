package akeefer.service;

import org.springframework.stereotype.Service;

@Service
public class PersonService {

    public String createPersonScript() {
        return "var personen = [\n" +
                "        {id: 'andi', distance: 1500000, done: false},\n" +
                "        {id: 'sabine', distance: 500000, done: false},\n" +
                "//        {id: 'uli_hans', distance: 1000000, done: false},\n" +
                "        {id: 'roland', distance: 2500000, done: false},\n" +
                "        {id: 'norbert', distance: 2000000, done: false}\n" +
                "    ];";
    }
}
