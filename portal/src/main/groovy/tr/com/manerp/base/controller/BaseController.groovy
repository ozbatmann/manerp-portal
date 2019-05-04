package tr.com.manerp.base.controller

import manerp.response.plugin.response.ManeResponseController
import org.springframework.validation.Errors

class BaseController extends ManeResponseController {

    private static String parseDefaultMessage(String defaultMessage) {

        String result = ''
        String uniquePattern = 'unique'
        String nullPattern = 'null'
        String customPattern = 'custom'
        String constructorPattern = 'constructor' // id ile ilgili hata TODO: id expose edilmezse iyi olur

        if ( defaultMessage.contains(uniquePattern) ) {

            result = 'sistemde bulunduğu için reddedildi.'

        } else if ( defaultMessage.contains(nullPattern) ) {

            result = 'değer uygun olmadığı için reddedildi.'

        } else if ( defaultMessage.contains(customPattern) ) {

            result = 'değer uygun olmadığı için reddedildi.'

        } else if ( defaultMessage.contains(constructorPattern) ) {

            result = 'değeri sistemde bulunmadığı için reddedildi.'
        }

        return result
    }

    protected static String parseValidationErrors(Errors errors) {

        StringBuilder stringBuilder = new StringBuilder()

        errors.allErrors.each { item ->

            stringBuilder.append("${item.field} alanı için sağlanan '${item.rejectedValue}' ${parseDefaultMessage(item.defaultMessage)}\n")
        }

        return stringBuilder.toString()
    }
}
