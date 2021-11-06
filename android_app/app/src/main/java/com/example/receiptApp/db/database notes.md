# Database notes

- Can't be deleted an enelement child of an aggregate with only one elemente, must be deletted the aggregate.

- The method for deleting one element is inside PublicElementsDao, the method for add an element inside an existing Aggregate
  is inside the PublicAggregateDao. "ci scusiamo per il disagio".