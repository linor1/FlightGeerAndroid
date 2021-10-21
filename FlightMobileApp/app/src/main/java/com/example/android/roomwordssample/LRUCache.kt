package com.example.android.roomwordssample

public class LRUCache() {

    private val map = hashMapOf<String, Node>()
    private val head: Node = Node("Start", "Start")
    private val tail: Node = Node("End", "End")

    init {
        head.next = tail
        tail.prev = head
    }

    fun get(key: String): String {
        if (map.containsKey(key)) {
            val node = map[key]!!
            remove(node)
            addAtEnd(node)
            return node.value
    }
        return "Err"
    }
    fun getLinkedList(): Node{
        return tail;
    }
    fun put(key: String, value: String) {
        if (map.containsKey(key)) {
            remove(map[key]!!)
        }
        val node = Node(key, value)
        addAtEnd(node)
        map[key] = node
        if (map.size > 5) {
            val first = head.next!!
            remove(first)
            map.remove(first.key)
        }
    }

    private fun remove(node: Node) {
        val next = node.next!!
        val prev = node.prev!!
        prev.next = next
        next.prev = prev
    }

    private fun addAtEnd(node: Node) {
        val prev = tail.prev!!
        prev.next = node
        node.prev = prev
        node.next = tail
        tail.prev = node
    }

    data class Node(val key: String, val value: String) {
        var next: Node? = null
        var prev: Node? = null
    }
}
