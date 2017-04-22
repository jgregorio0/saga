package com.jesus.opencms.util

import groovy.json.JsonBuilder


def r1 = [f1: "r1f1", f2: "r1f2", f3: "r1f3"]
def r2 = [f1: "r2f1", f2: "r2f2"]
def resources = [r1, r2]
def fields = ["f1", "f2"]
//		jsonArray : [{f1:r1[f1], f2:r1[f2]},{f1:r2[f1], f2:r2[f2]}]

//def jArray = results.inject([]){ result, solrRes, i ->
//    result[i] = fields.inject([:]){ json, field ->
//        json[field] = solrRes.getField(field)
//        json
//    }
//    result;
//}

def list = []
for (int i = 0; i < resources.size(); i++) {
    def res = resources[i];
    def map = fields.inject([:]) { json, field ->
        json[field] = res[field]
        json
    }
    list.add(map);
}
println new JsonBuilder(list).toString();