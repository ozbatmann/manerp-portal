package tr.com.manerp.redis

import manerp.response.plugin.response.ManeResponse
import org.grails.web.json.JSONArray
import tr.com.manerp.base.controller.BaseController
import tr.com.manerp.enumeration.RedisSyncType

class RedisController extends BaseController {

    static namespace = "v1"
   RedisSyncService redisSyncService

    def index() { }

    def synchronizeRedisWithDB(){

        ManeResponse maneResponse = new ManeResponse()
        try {
            JSONArray result = redisSyncService.synchronizeRedisWithDB(
                    request.JSON.orgId.toString(),
                    request.JSON.username.toString(),
                    RedisSyncType.valueOf(request.JSON.redisSyncType),
                    request.JSON.key.toString())

            maneResponse.setData(result)
        }

        catch (Exception e) {

            throw new Exception(e.getMessage())
        }
        render maneResponse
    }
}
