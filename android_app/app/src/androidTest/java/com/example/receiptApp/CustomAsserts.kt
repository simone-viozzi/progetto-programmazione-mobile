package com.example.receiptApp

import com.example.receiptApp.db.aggregate.Aggregate
import com.example.receiptApp.db.element.Element
import org.junit.Assert

class CustomAsserts{
    companion object {
        fun aggregates(obj1: Aggregate, obj2: Aggregate) {

            Assert.assertEquals(obj1.tag_id, obj2.tag_id)
            Assert.assertTrue(obj1.date == obj2.date)
            Assert.assertTrue(obj1.location.toString() == obj2.location.toString())
            Assert.assertTrue(obj1.attachment == obj2.attachment)
            Assert.assertEquals(obj1.total_cost, obj2.total_cost)
        }

        fun elements(obj1: Element, obj2: Element){

            Assert.assertEquals(obj1.name, obj2.name)
            Assert.assertEquals(obj1.num, obj2.num)
            Assert.assertEquals(obj1.parent_tag_id, obj2.parent_tag_id)
            Assert.assertEquals(obj1.tag_id, obj2.tag_id)
            Assert.assertEquals(obj1.cost, obj2.cost)
        }

        fun elementsList(list1: List<Element>?, list2: List<Element>?){

            if(list1 == null || list2 == null) {
                Assert.fail("element list comparison failed due to null list.")
                return
            }

            Assert.assertEquals(list1.size, list2.size)

            for(i in list1.indices){
                elements(list1[i], list2[i])
            }
        }
    }
}