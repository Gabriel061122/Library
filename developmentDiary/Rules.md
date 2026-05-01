
#### User
- cannot have an empty id, name, or type
- Only one active borrowing
	- If a borrowing is overdue, then the user cannot borrow another book and has to pay a fraction of the book's borrowing price 
- as many buys as he wants
	- Can't buy without sufficient money
- Only one reservation
#### Books
- Cannot have and empty ISBN, Title, Author, price or publishing date
- Cannot have negative stock

#### Borrowing Copies
- If every borrowing copy is unavaliable, then borrowing is canceled

#### Borrowing
- If the start date has not reached yet, then it is treated as a reservation
- If a borrowing is overdue, then the user cannot borrow another book and has to pay a fraction of the book's borrowing price 

#### Reservation
- is a borrow that automatically counts as a borrowing if the user fulfill every prerequisite, independently of the user picking up the book or not.
- If a user does not fulfill every prerequisite, then the reservation is cancelled. 

#### Buy
- A buy cannot procede if:
	- a user does not have enough money
	- there is no stock of the book

#### Card Information
- Is an information container
- Must validate it's internal information coherency.



