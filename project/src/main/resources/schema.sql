PRAGMA foreign_keys = ON;


CREATE TABLE IF NOT EXISTS Categories (
                                          CategoryID INTEGER PRIMARY KEY,
                                          CategoryName TEXT NOT NULL UNIQUE,
                                          Description TEXT
);


CREATE TABLE IF NOT EXISTS Routines (
                                        RoutineID INTEGER PRIMARY KEY,
                                        RoutineName TEXT NOT NULL UNIQUE
);


CREATE TABLE IF NOT EXISTS RoutineElements (
                                               RoutineElementID INTEGER PRIMARY KEY,
                                               ElementName TEXT NOT NULL
);


CREATE TABLE IF NOT EXISTS RoutineTimes (
                                            RoutineID INTEGER NOT NULL,
                                            RoutineElementID INTEGER NOT NULL,
                                            Weekday TEXT NOT NULL
                                                CHECK (Weekday IN ('MON','TUE','WED','THU','FRI','SAT','SUN')),
                                            StartMinute INTEGER NOT NULL
                                                CHECK (StartMinute BETWEEN 0 AND 1440),
                                            EndMinute INTEGER NOT NULL
                                                CHECK (EndMinute BETWEEN 0 AND 1440),
                                            CHECK (EndMinute > StartMinute),

                                            PRIMARY KEY (RoutineID, Weekday, StartMinute),

                                            FOREIGN KEY (RoutineID)
                                                REFERENCES Routines(RoutineID)
                                                ON DELETE CASCADE,

                                            FOREIGN KEY (RoutineElementID)
                                                REFERENCES RoutineElements(RoutineElementID)
                                                ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS Tasks (
                                     TaskID INTEGER PRIMARY KEY,
                                     CategoryID INTEGER,
                                     TaskName TEXT NOT NULL UNIQUE,
                                     Description TEXT,
                                     ExpectedDuration TEXT,
                                     GoalEndTime TEXT,      -- ISO 8601: 2026-02-14T18:30
                                     Deadline TEXT,         -- ISO 8601

                                     FOREIGN KEY (CategoryID)
                                         REFERENCES Categories(CategoryID)
                                         ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS TasksToRoutine (
                                              RoutineElementID INTEGER NOT NULL,
                                              TaskID INTEGER NOT NULL,

                                              PRIMARY KEY (RoutineElementID, TaskID),

                                              FOREIGN KEY (RoutineElementID)
                                                  REFERENCES RoutineElements(RoutineElementID)
                                                  ON DELETE CASCADE,

                                              FOREIGN KEY (TaskID)
                                                  REFERENCES Tasks(TaskID)
                                                  ON DELETE CASCADE
);


CREATE INDEX IF NOT EXISTS idx_routinetimes_weekday
    ON RoutineTimes(Weekday);

CREATE INDEX IF NOT EXISTS idx_routinetimes_routine
    ON RoutineTimes(RoutineID);

CREATE INDEX IF NOT EXISTS idx_taskstoroutine_task
    ON TasksToRoutine(TaskID);