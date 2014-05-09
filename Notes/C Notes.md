### Operators

##### 4/6/14

#### Comma Operator

[Wikipedia: Comma Operator](http://en.wikipedia.org/wiki/Comma_operator)

* Evaluates its first operand and discards the result
* Then evaluates the second operand and returns this value (and type).

**Precedence:** *follows* assignment

    i = (a, b);  // stores b into i 
    i = a, b;    // stores a into i. Equivalent to (i = a), b;
                    
### Escape Sequences

##### 4/6/14

I'm having a hard time finding good documentation of these things.
By experiment here is what I know:

* What does `puts("\e[2J\e[H");` do?
   * **puts**: print string followed by newline (see <a href=#puts>link to `puts`</a>)
   * \e[2J: clears the screen by inserting enough newlines that the old content is off the screen
   * \e[H:  in this case, it changes the clearing of the screen by `e[2J` so that it only involves
            writing one newline and scrolling the screen itself so the old content is no longer
            visible

### stdio.h

##### 4/6/14

#### puts

    int puts(const char *str)

* **does:** prints the given `string`, followed by a `newline`
* **returns:** nonnegative value if successful, `EOF` on error

##### 3/19/14

#### memset

    void *memset(void *str, int c, size_t n)

* **does:** fills `n` bytes of `str` with `n` `(unsigned char)c`s
* **returns:** `str` (possibly so that you can chain calls to different functions together)

### MACROS

#### Newline in macro

##### 4/29/14

Use the `\` character, but you need a space before it unless you want there to
be no space between the letters

	a\
	b  // => ab
	
	a \
	b 	// => a b

#### Macro that does nothing

##### 3/15/14

To define a macro that does nothing:

    #define TAKES_A_PARAMETER_AND_THEN_TURNS_TO_THIN_AIR(X)

#### Using do {} while(0) for great scott

##### 3/15/14

[Reference: SO](http://stackoverflow.com/questions/1067226/c-multi-line-macro-do-while0-vs-scope-block)

If you want to use a macro that calls multiple function within an `if` statement,
you'll be in for a bit of a surprise.

We define the naive

    #define CALL_FUNCS(x) \
    func1(x); \
    func2(x); \
    func3(x);

Then we call it with

    if (<condition>)
        CALL_FUNCS(a);
    else
        bar(a);

But that gets expanded to the following **problematic code**

    if (<condition>)
        func1(x);
    func2(x);
    func3(x);

    // NO! this else now doesn't belong to anything!
    else
        bar(a);

So you need to put the func's in some sort of block, but you want to make sure that
it gets executed exactly once. This is precicely what `do {...} while (0)`
accomplishes.

So we instead define

    #define CALL_FUNCS(x) \
    do { \
    func1(x); \
    func2(x); \
    func3(x); \
    } while (0)

Which will be expanded to

    if (<condition>)
        do {
            func1(x);
            func2(x);
            func3(x);
        } while (0);

    else
        bar(a);

which works as we expect!



### string.h

##### 3/26/14

#### strchr

    char *strchr(char *str, int character)

* **Returns** a pointer to the first occurrence of `character` in `str`
    * Returns `NULL` if `character` not in `str`

##### 2/27/14

#### strcpy

    char *strcpy(char *dest, char *src)

* **Returns** `dest`
* Also copies null-terminator.

#### strncpy

        char *strncpy(char *dest, char *src, size_t count)

* Only copies `count` characters
    * `if count ≤ strlen(src):` `dest` will not have a null-terminator
    * `else:` Will be padded with nulls
* **Returns** `dest`

### stdio.h

    int sprintf(char *buffer, char *format, ...)

* Basically, it calls `printf` on the `format` argument, but instead of writing the output to `stdout`, it writes it into the `buffer` argument
* **Returns** the number of characters written, or a negative value if there was an error
