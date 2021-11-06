package com.example.receiptApp

import com.example.receiptApp.db.aggregate.Aggregate
import com.example.receiptApp.db.element.Element
import org.junit.Assert
import kotlin.math.abs

class CustomAsserts{
    companion object {
        fun aggregates(
            obj1: Aggregate,
            obj2: Aggregate,
            checkTags: Boolean = false,
            checkTagsIds: Boolean = false,
            checkCost: Boolean = false
        ) {
            if(checkTags) Assert.assertEquals(obj1.tag, obj2.tag)
            if(checkTagsIds) Assert.assertEquals(obj1.tag_id, obj2.tag_id)
            Assert.assertTrue(obj1.date == obj2.date)
            Assert.assertTrue(obj1.location.toString() == obj2.location.toString())
            Assert.assertTrue(obj1.attachment == obj2.attachment)
            if(checkCost)Assert.assertTrue(abs(obj1.total_cost - obj2.total_cost) < 0.001f)
        }

        fun elements(
            obj1: Element,
            obj2: Element,
            checkTags: Boolean = false,
            checkTagsIds: Boolean = false
        ){

            Assert.assertEquals(obj1.name, obj2.name)
            Assert.assertEquals(obj1.num, obj2.num)
            if(checkTags) {
                Assert.assertEquals(obj1.parent_tag, obj2.parent_tag)
                Assert.assertEquals(obj1.elem_tag, obj2.elem_tag)
            }
            if(checkTagsIds) {
                Assert.assertEquals(obj1.parent_tag_id, obj2.parent_tag_id)
                Assert.assertEquals(obj1.elem_tag_id, obj2.elem_tag_id)
            }
            Assert.assertTrue(abs(obj1.cost - obj2.cost) < 0.001f)
        }

        /**
         * Elements list
         *
         * This function
         *
         * @param list1
         * @param list2
         * @param checkTags
         * @param checkTagsIds
         */
        fun elementsList(
            list1: List<Element>?,
            list2: List<Element>?,
            checkTags: Boolean = false,
            checkTagsIds: Boolean = false
        ){

            if(list1 == null || list2 == null) {
                Assert.fail("element list comparison failed due to null list.")
                return
            }

            Assert.assertEquals(list1.size, list2.size)

            for(i in list1.indices){
                elements(list1[i], list2[i], checkTags, checkTagsIds)
            }
        }

        /**
         * Compare aggregate and elements
         *
         * This function check if the related parameters between
         * aggregates and elements match correctly
         *
         * @param aggregate
         * @param elemList
         */
        fun compareAggregateAndElements(aggregate: Aggregate, elemList: List<Element>){
            var expectdTotalCost = 0.0f
            elemList.forEach {
                Assert.assertEquals(aggregate.id, it.aggregate_id)
                Assert.assertEquals(aggregate.tag_id, it.parent_tag_id)
                Assert.assertEquals(aggregate.tag, it.parent_tag)
                expectdTotalCost += it.cost * it.num
            }
            Assert.assertTrue(abs(aggregate.total_cost - expectdTotalCost) < 0.001f)
        }
    }
}