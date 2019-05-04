package manerp.portal

class UrlMappings {

    static mappings = {


        "/api/$namespace/$controller"() {
            action = [GET: "index", POST: "save", PUT: "update", PATCH: "patch", DELETE: "delete"]
        }

        "/api/$namespace/$controller/$id"() {
            action = [GET: "show", PUT: "update", PATCH: "patch", DELETE: "delete"]
        }

        "/api/$namespace/$controller/$id/$sub"(method: "GET") {
            action = { params.sub }
        }

        "/api/$namespace/$controller/$action"(method: "POST") {
            action = $action
        }

        "/$namespace/$controller/$action?/$id?(.$format)?" {
            namespace = {
                params.namespace
            }
        }

        "/"(uri: "/index.html")
        "500"(view: '/error')
        "404"(view: '/notFound')
    }
}
