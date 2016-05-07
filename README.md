# IAED_Projeto2

## TADs

### list.h
- struct listnode
- type listlink
- struct list
- listInit()
- newListNode(Item)
- listInsert(List, Item)
- listSearch(List, Key)
- listDelete(List, Item)
- listFree(List)

### hash.h
- struct SThash
- STinit()
- STfree(SThash)
- STsearch(SThash, Key)
- STdelete(SThash, Item)
- STinsert(SThash, Item)

### bintree.h
- struct BTree
- btreeInit()
- newBTreeNode(Item)
- btreeSearch(BTree, Key)
- btreeInsert(BTree, Item)
- btreeMax(BTree)
- btreeHeight(BTree)
- btreeTraverse(BTree, void (\*visit)(Item))
- btreeFree(BTree)


### item.h
- struct hashtag
- type Item
- type Key
- itemInit(Key)
- itemcmp(Item, Item)
- itemfree(Item)
- hashfunc(Key)
