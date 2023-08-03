package com.lovelycatv.ark.test.entites

import com.lovelycatv.ark.common.annotations.Entity

@Entity(tableName = "works")
class Work {
    var id: Int = 0
    var userId: Int = 0
    var workName: String? = null
    var hot: Double = 0.0
}