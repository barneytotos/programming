THIS PROJECT HAS BEEN ABANDONED
===============================

I'm no longer using the spreadsheet it was going to plug into.

Sample Usages
-------------

```bash
    # list tasks, with call-numbers for each
    $ mcl ls

    # list group’s tasks, with call-numbers for each
    $ mcl lsGroup NN

    # ls, but with time totals, start dates, due dates, etc
    $ mcl ll

    # add task if it doesn’t exist
    $ mcl addTask NN --task HW_3 --duedate 10/23

    # add group if it doesn’t exist
    $ mcl addGroup Alg

    # begin counting for first task
    $ mcl begin 1

    # adds task if it doesn’t exist
    $ mcl begin NN HW_3

    # stops counting
    $ mcl end

    # move start time forward 15 mins
    $ mcl move 15

    # move start time backward 15 mins
    $ mcl move -15

    # print tabulated vrsn of today’s CSV w/ line#s
    $ mcl print

    # delete line 4 of CSV printed above
    $ mcl remove 4

    # remove task 1, check off associated Reminder
    $ mcl finish 1

    # remove task 1, delete associated Reminder
    $ mcl delete 1

    # print time for current task (block, day, total)
    $ mcl show

    # post-hoc add time a task was done
    $ mcl did NN HW_3 10:30 15:15

    # cancel current task
    $ mcl cancel
```


DIR STRUCTURE
-------------
```
Days/
    10-17-2013.csv
    10-18-2013.csv
    10-19-2013.csv

Tasks/
    NN/
        HW_3.task
        Ch 16.task

    Alg/
```

CSV FILES
---------
```
group, task, location, start time, end time, block time
group, task, location, start time, end time, block time
...
```

TASK FILES
----------
```
start date
due date (can be blank)
time for block 1
...
time for block n
```

