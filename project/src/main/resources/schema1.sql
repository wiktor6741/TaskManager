PRAGMA foreign_keys = ON;
PRAGMA user_version = 1;

CREATE TABLE IF NOT EXISTS categories (
                                          category_id INTEGER PRIMARY KEY,
                                          category_name TEXT NOT NULL UNIQUE,
                                          description TEXT,
                                          created_at TEXT NOT NULL,
                                          updated_at TEXT NOT NULL,
                                          deleted_at TEXT
);


CREATE TABLE IF NOT EXISTS routines (
                                        routine_id INTEGER PRIMARY KEY,
                                        routine_name TEXT NOT NULL UNIQUE,
                                        week_count INTEGER NOT NULL,
                                        created_at TEXT NOT NULL,
                                        updated_at TEXT NOT NULL,
                                        deleted_at TEXT
);


CREATE TABLE IF NOT EXISTS routine_elements (
                                               routine_element_id INTEGER PRIMARY KEY,
                                               element_id TEXT NOT NULL,
                                               description TEXT
                                               created_at TEXT NOT NULL,
                                               updated_at TEXT NOT NULL,
                                               deleted_at TEXT
);


CREATE TABLE IF NOT EXISTS routine_times (
                                            routine_id INTEGER NOT NULL,
                                            routine_element_id INTEGER NOT NULL,
                                            week_num INTEGER NOT NULL,
                                            weekday TEXT NOT NULL
                                                CHECK (weekday IN ('MON','TUE','WED','THU','FRI','SAT','SUN')),
                                            start_time TEXT NOT NULL,
                                            end_time TEXT NOT NULL,
                                            created_at TEXT NOT NULL,
                                            updated_at TEXT NOT NULL,
                                            deleted_at TEXT


                                            PRIMARY KEY (routine_id, week_num, weekday, start_time),

                                            FOREIGN KEY (routine_id)
                                                REFERENCES Routines(routine_id)
                                                ON DELETE CASCADE,

                                            FOREIGN KEY (routine_element_id)
                                                REFERENCES RoutineElements(routine_element_id)
                                                ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS tasks (
                                     task_id INTEGER PRIMARY KEY,
                                     category_id INTEGER,
                                     task_name TEXT NOT NULL UNIQUE,
                                     description TEXT,
                                     expected_duration TEXT,
                                     goal_end_time TEXT,      -- ISO 8601: 2026-02-14T18:30
                                     deadline TEXT,         -- ISO 8601
                                     priority INTEGER,
                                     created_at TEXT NOT NULL,
                                     updated_at TEXT NOT NULL,
                                     deleted_at TEXT,
                                     completed_at TEXT

                                     FOREIGN KEY (category_id)
                                         REFERENCES Categories(category_id)
                                         ON DELETE SET NULL
);


CREATE TABLE IF NOT EXISTS task_calendar (
                                              task_id INTEGER NOT NULL,
                                              start_time TEXT NOT NULL,
                                              end_time TEXT NOT NULL,
                                              created_at TEXT NOT NULL,
                                              updated_at TEXT NOT NULL,
                                              deleted_at TEXT
);
