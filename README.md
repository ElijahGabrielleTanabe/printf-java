# PRINTF IN JAVA

My interpretation and implementation of C's printf in Java. This is just a fun little side project and <ins>**should not be used in any pre-exisiting projects**</ins> as this is a simple implementation with flaws and better alternatives exist (Java's System.out.format).

## Usage

Create a new Printf object and use the `print(String s, Object ... args)` method passing in a string with format specifiers and the associated values. <br/>

ex. p.print("Hello %-4s!", "world"); <br/>

Format specifiers strictly follow this syntax: ***%\[flags][width][.precision]type***<br/>

* **Flags:** +-0 (any combination)
* **Width:** int
* **Precision:** .int
* **Type:** int (d), float/double(f), string(s), char(c)<br/>

ex. %-10.3s, %+0.5f, %020c, %+010.5d <br/>
Sub-specifier effects can be found [here](https://cplusplus.com/reference/cstdio/printf/)

You can get the last formated print using `getLastPrint()`

## To-do
- [ ] **Parameter field:** Matching format specifiers to values with a numberic value.
- [ ] **' flag:** Thousands seperator
- [ ] **\* value:** Matching with values in sequential order