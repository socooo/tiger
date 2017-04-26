#include <stdio.h>
#include <stdlib.h>
#include <string.h>

// The Gimple Garbage Collector.


//===============================================================//
// The Java Heap data structure.

/*   
      ----------------------------------------------------
      |                        |                         |
      ----------------------------------------------------
      ^\                      /^
      | \<~~~~~~~ size ~~~~~>/ |
    from                       to
 */
struct JavaHeap
{
  int size;         // in bytes, note that this if for semi-heap size
  char *from;       // the "from" space pointer
  char *fromFree;   // the next "free" space in the from space
  char *to;         // the "to" space pointer
  char *toStart;    // "start" address in the "to" space
  char *toNext;     // "next" free space pointer in the to space
};

// The Java heap, which is initialized by the following
// "heap_init" function.
struct JavaHeap heap;

// Lab 4, exercise 10:
// Given the heap size (in bytes), allocate a Java heap
// in the C heap, initialize the relevant fields.
void Tiger_heap_init (int heapSize)
{
  // You should write 7 statement here:
  // #1: allocate a chunk of memory of size "heapSize" using "malloc"
  heap = *(struct JavaHeap *)malloc(heapSize);
  // #2: initialize the "size" field, note that "size" field
  // is for semi-heap, but "heapSize" is for the whole heap.
  memset(heap, 0, heapSize);
  heap.size = heapSize/2;
  // #3: initialize the "from" field (with what value?)
  heap.from = heap;
  // #4: initialize the "fromFree" field (with what value?)
  heap.fromFree = heap;
  // #5: initialize the "to" field (with what value?)
  heap.to = heap + heap.size;
  // #6: initizlize the "toStart" field with NULL;
  heap.toStart = heap + heap.size;
  // #7: initialize the "toNext" field with NULL;
  heap.toNext = heap + heap.size;
  return;
}

// The "prev" pointer, pointing to the top frame on the GC stack. 
// (see part A of Lab 4)
void *prev = 0;



//===============================================================//
// Object Model And allocation


// Lab 4: exercise 11:
// "new" a new object, do necessary initializations, and
// return the pointer (reference).
/*    ----------------
      | vptr      ---|----> (points to the virtual method table)
      |--------------|
      | isObjOrArray | (0: for normal objects)
      |--------------|
      | length       | (this field should be empty for normal objects)
      |--------------|
      | forwarding   | 
      |--------------|\
p---->| v_0          | \      
      |--------------|  s
      | ...          |  i
      |--------------|  z
      | v_{size-1}   | /e
      ----------------/
*/
// Try to allocate an object in the "from" space of the Java
// heap. Read Tiger book chapter 13.3 for details on the
// allocation.
// There are two cases to consider:
//   1. If the "from" space has enough space to hold this object, then
//      allocation succeeds, return the apropriate address (look at
//      the above figure, be careful);
//   2. if there is no enough space left in the "from" space, then
//      you should call the function "Tiger_gc()" to collect garbages.
//      and after the collection, there are still two sub-cases:
//        a: if there is enough space, you can do allocations just as case 1; 
//        b: if there is still no enough space, you can just issue
//           an error message ("OutOfMemory") and exit.
//           (However, a production compiler will try to expand
//           the Java heap.)
void *Tiger_new (void *vtable, int size)
{
  // Your code here:
  if(heap.from_free + size > heap.size){
      Tiger_gc();
      if(heap.from_free + size > heap.size){
        printf("no space.\n");
        exit();
      }
    }
  int *p = heap.fromFree;
  heap.fromFree += size;
  memset(p, 0, size);
  memcpy(p, &vtable, sizeof(void*));
  p[1] = 0;
  p = &p[4];
  return p;
}

// "new" an array of size "length", do necessary
// initializations. And each array comes with an
// extra "header" storing the array length and other information.
/*    ----------------
      | vptr         | (this field should be empty for an array)
      |--------------|
      | isObjOrArray | (1: for array)
      |--------------|
      | length       |
      |--------------|
      | forwarding   | 
      |--------------|\
p---->| e_0          | \      
      |--------------|  s
      | ...          |  i
      |--------------|  z
      | e_{length-1} | /e
      ----------------/
*/
// Try to allocate an array object in the "from" space of the Java
// heap. Read Tiger book chapter 13.3 for details on the
// allocation.
// There are two cases to consider:
//   1. If the "from" space has enough space to hold this array object, then
//      allocation succeeds, return the apropriate address (look at
//      the above figure, be careful);
//   2. if there is no enough space left in the "from" space, then
//      you should call the function "Tiger_gc()" to collect garbages.
//      and after the collection, there are still two sub-cases:
//        a: if there is enough space, you can do allocations just as case 1; 
//        b: if there is still no enough space, you can just issue
//           an error message ("OutOfMemory") and exit.
//           (However, a production compiler will try to expand
//           the Java heap.)
void *Tiger_new_array (int length)
{
  int new_array_size = sizeof(int) * (length + 3)+ sizeof(unsigned);
  if(heap.from_free + new_array_size > heap.size){
    Tiger_gc();
    if(heap.from_free + new_array_size > heap.size){
      printf("no space.\n");
      exit();
    }
  }
  int *p = heap.fromFree;
  heap.fromFree += new_array_size;
  memset(p, 0, new_array_size);
  p[2] = length;
  p[1] = 1;
  int* ret = &p[4];
  return ret;
}

//===============================================================//
// The Gimple Garbage Collector

// Lab 4, exercise 12:
// A copying collector based-on Cheney's algorithm.
static void Tiger_gc ()
{
  while(prev != 0){
    prev
  }
}

void copy_collect(){

}

void locate_args(){

}

void locate_locals(){

}

void locate_fields(){

}