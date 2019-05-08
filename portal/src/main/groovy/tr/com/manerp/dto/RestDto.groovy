package tr.com.manerp.dto

class RestDto {
    List itemList
    int status
    String message
    int itemCount

    RestDto(){
        this.status = 200
        this.message = ''
        this.itemCount = 0
        this.itemList = new ArrayList()
    }
}
