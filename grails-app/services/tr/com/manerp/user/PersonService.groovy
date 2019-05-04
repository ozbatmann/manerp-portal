package tr.com.manerp.user

import grails.gorm.transactions.Transactional

@Transactional
class PersonService {

    def save(Person person) {
       return person.save(flush: true, failOnError: true)
    }
    def update(Person person) {
        person.save(flush: true, failOnError: true)
    }
}
