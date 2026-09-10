ALTER TABLE Categories RENAME TO categories_tmp;
ALTER TABLE categories_tmp RENAME TO categories;
ALTER TABLE Routines RENAME TO routines_tmp;
ALTER TABLE routines_tmp RENAME TO routines;
ALTER TABLE RoutineElements RENAME TO routine_elements_tmp;
ALTER TABLE routine_elements_tmp RENAME TO routine_elements;
ALTER TABLE RoutineTimes RENAME TO routine_times_tmp;
ALTER TABLE routine_times_tmp RENAME TO routine_times;
ALTER TABLE Tasks RENAME TO tasks_tmp;
ALTER TABLE tasks_tmp RENAME TO tasks;

ALTER TABLE categories RENAME COLUMN CategoryID TO category_id;
ALTER TABLE categories RENAME COLUMN CategoryName TO category_name;
ALTER TABLE categories RENAME COLUMN Description TO description_tmp;
ALTER TABLE categories RENAME COLUMN description_tmp TO description;

ALTER TABLE routines RENAME COLUMN RoutineID TO routine_id;
ALTER TABLE routines RENAME COLUMN RoutineName TO routine_name;
ALTER TABLE routines RENAME COLUMN WeekCount TO week_count;

ALTER TABLE routine_elements RENAME COLUMN RoutineElementID TO routine_element_id;
ALTER TABLE routine_elements RENAME COLUMN ElementName TO element_name;
ALTER TABLE routine_elements RENAME COLUMN Description TO description_tmp;
ALTER TABLE routine_elements RENAME COLUMN description_tmp TO description;

ALTER TABLE routine_times RENAME COLUMN RoutineID TO routine_id;
ALTER TABLE routine_times RENAME COLUMN RoutineElementID TO routine_element_id;
ALTER TABLE routine_times RENAME COLUMN WeekNum TO week_num;
ALTER TABLE routine_times RENAME COLUMN Weekday TO weekday_tmp;
ALTER TABLE routine_times RENAME COLUMN weekday_tmp TO weekday;
ALTER TABLE routine_times RENAME COLUMN StartTime TO start_time;
ALTER TABLE routine_times RENAME COLUMN EndTime TO end_time;

ALTER TABLE tasks RENAME COLUMN TaskID TO task_id;
ALTER TABLE tasks RENAME COLUMN CategoryID TO category_id;
ALTER TABLE tasks RENAME COLUMN TaskName TO task_name;
ALTER TABLE tasks RENAME COLUMN Description TO description_tmp;
ALTER TABLE tasks RENAME COLUMN description_tmp TO description;
ALTER TABLE tasks RENAME COLUMN ExpectedDuration TO expected_duration;
ALTER TABLE tasks RENAME COLUMN GoalEndTime TO goal_end_time;
ALTER TABLE tasks RENAME COLUMN Deadline TO deadline_tmp;
ALTER TABLE tasks RENAME COLUMN deadline_tmp TO deadline;
ALTER TABLE tasks RENAME COLUMN Priority TO priority_tmp;
ALTER TABLE tasks RENAME COLUMN priority_tmp TO priority;

ALTER TABLE categories ADD COLUMN created_at TEXT NOT NULL DEFAULT '1970-01-01T00:00:00';
ALTER TABLE categories ADD COLUMN updated_at TEXT NOT NULL DEFAULT '1970-01-01T00:00:00';
ALTER TABLE categories ADD COLUMN deleted_at TEXT DEFAULT NULL;

ALTER TABLE routines ADD COLUMN created_at TEXT NOT NULL DEFAULT '1970-01-01T00:00:00';
ALTER TABLE routines ADD COLUMN updated_at TEXT NOT NULL DEFAULT '1970-01-01T00:00:00';
ALTER TABLE routines ADD COLUMN deleted_at TEXT DEFAULT NULL;

ALTER TABLE routine_elements ADD COLUMN created_at TEXT NOT NULL DEFAULT '1970-01-01T00:00:00';
ALTER TABLE routine_elements ADD COLUMN updated_at TEXT NOT NULL DEFAULT '1970-01-01T00:00:00';
ALTER TABLE routine_elements ADD COLUMN deleted_at TEXT DEFAULT NULL;

ALTER TABLE routine_times ADD COLUMN created_at TEXT NOT NULL DEFAULT '1970-01-01T00:00:00';
ALTER TABLE routine_times ADD COLUMN updated_at TEXT NOT NULL DEFAULT '1970-01-01T00:00:00';
ALTER TABLE routine_times ADD COLUMN deleted_at TEXT DEFAULT NULL;

ALTER TABLE tasks ADD COLUMN created_at TEXT NOT NULL DEFAULT '1970-01-01T00:00:00';
ALTER TABLE tasks ADD COLUMN updated_at TEXT NOT NULL DEFAULT '1970-01-01T00:00:00';
ALTER TABLE tasks ADD COLUMN deleted_at TEXT DEFAULT NULL;
ALTER TABLE tasks ADD COLUMN completed_at TEXT DEFAULT NULL;


UPDATE categories       SET created_at = strftime('%Y-%m-%dT%H:%M:00', 'now'), updated_at = strftime('%Y-%m-%dT%H:%M:00', 'now');
UPDATE routines         SET created_at = strftime('%Y-%m-%dT%H:%M:00', 'now'), updated_at = strftime('%Y-%m-%dT%H:%M:00', 'now');
UPDATE routine_elements SET created_at = strftime('%Y-%m-%dT%H:%M:00', 'now'), updated_at = strftime('%Y-%m-%dT%H:%M:00', 'now');
UPDATE routine_times    SET created_at = strftime('%Y-%m-%dT%H:%M:00', 'now'), updated_at = strftime('%Y-%m-%dT%H:%M:00', 'now');
UPDATE tasks             SET created_at = strftime('%Y-%m-%dT%H:%M:00', 'now'), updated_at = strftime('%Y-%m-%dT%H:%M:00', 'now');


DROP TABLE IF EXISTS TasksToRoutine;

CREATE TABLE IF NOT EXISTS task_calendar (
                                             task_id          INTEGER NOT NULL,
                                             start_time       TEXT NOT NULL,
                                             end_time         TEXT NOT NULL,
                                             created_at       TEXT NOT NULL DEFAULT (strftime('%Y-%m-%dT%H:%M:00', 'now')),
    updated_at       TEXT NOT NULL DEFAULT (strftime('%Y-%m-%dT%H:%M:00', 'now')),
    deleted_at       TEXT,


    PRIMARY KEY (task_id, start_time),
    FOREIGN KEY (task_id) REFERENCES tasks(task_id) ON DELETE CASCADE
)
;


CREATE TRIGGER trg_categories_updated_at
    AFTER UPDATE ON categories
BEGIN
    UPDATE categories SET updated_at = strftime('%Y-%m-%dT%H:%M:00', 'now') WHERE category_id = NEW.category_id;
END;

CREATE TRIGGER trg_routines_updated_at
    AFTER UPDATE ON routines
BEGIN
    UPDATE routines SET updated_at = strftime('%Y-%m-%dT%H:%M:00', 'now') WHERE routine_id = NEW.routine_id;
END;

CREATE TRIGGER trg_routine_elements_updated_at
    AFTER UPDATE ON routine_elements
BEGIN
    UPDATE routine_elements SET updated_at = strftime('%Y-%m-%dT%H:%M:00', 'now') WHERE routine_element_id = NEW.routine_element_id;
END;

CREATE TRIGGER trg_routine_times_updated_at
    AFTER UPDATE ON routine_times
BEGIN
    UPDATE routine_times SET updated_at = strftime('%Y-%m-%dT%H:%M:00', 'now')
    WHERE routine_id = NEW.routine_id AND week_num = NEW.week_num
      AND weekday = NEW.weekday AND start_time = NEW.start_time;
END;

CREATE TRIGGER trg_tasks_updated_at
    AFTER UPDATE ON tasks
BEGIN
    UPDATE tasks SET updated_at = strftime('%Y-%m-%dT%H:%M:00', 'now') WHERE task_id = NEW.task_id;
END;

CREATE TRIGGER trg_task_calendar_updated_at
    AFTER UPDATE ON task_calendar
BEGIN
    UPDATE task_calendar SET updated_at = strftime('%Y-%m-%dT%H:%M:00', 'now') WHERE task_calendar_id = NEW.task_calendar_id;
END;




CREATE TRIGGER trg_categories_created_at
    AFTER INSERT ON categories
    WHEN NEW.created_at = '1970-01-01T00:00:00'
BEGIN
    UPDATE categories
    SET created_at = strftime('%Y-%m-%dT%H:%M:00', 'now'),
        updated_at = strftime('%Y-%m-%dT%H:%M:00', 'now')
    WHERE category_id = NEW.category_id;
END;

CREATE TRIGGER trg_routines_created_at
    AFTER INSERT ON routines
    WHEN NEW.created_at = '1970-01-01T00:00:00'
BEGIN
    UPDATE routines
    SET created_at = strftime('%Y-%m-%dT%H:%M:00', 'now'),
        updated_at = strftime('%Y-%m-%dT%H:%M:00', 'now')
    WHERE routine_id = NEW.routine_id;
END;

CREATE TRIGGER trg_routine_elements_created_at
    AFTER INSERT ON routine_elements
    WHEN NEW.created_at = '1970-01-01T00:00:00'
BEGIN
    UPDATE routine_elements
    SET created_at = strftime('%Y-%m-%dT%H:%M:00', 'now'),
        updated_at = strftime('%Y-%m-%dT%H:%M:00', 'now')
    WHERE routine_element_id = NEW.routine_element_id;
END;

CREATE TRIGGER trg_routine_times_created_at
    AFTER INSERT ON routine_times
    WHEN NEW.created_at = '1970-01-01T00:00:00'
BEGIN
    UPDATE routine_times
    SET created_at = strftime('%Y-%m-%dT%H:%M:00', 'now'),
        updated_at = strftime('%Y-%m-%dT%H:%M:00', 'now')
    WHERE routine_id = NEW.routine_id
      AND week_num = NEW.week_num
      AND weekday = NEW.weekday
      AND start_time = NEW.start_time;
END;

CREATE TRIGGER trg_tasks_created_at
    AFTER INSERT ON tasks
    WHEN NEW.created_at = '1970-01-01T00:00:00'
BEGIN
    UPDATE tasks
    SET created_at = strftime('%Y-%m-%dT%H:%M:00', 'now'),
        updated_at = strftime('%Y-%m-%dT%H:%M:00', 'now')
    WHERE task_id = NEW.task_id;
END;

CREATE TRIGGER trg_task_calendar_created_at
    AFTER INSERT ON task_calendar
    WHEN NEW.created_at = '1970-01-01T00:00:00'
BEGIN
    UPDATE task_calendar
    SET created_at = strftime('%Y-%m-%dT%H:%M:00', 'now'),
        updated_at = strftime('%Y-%m-%dT%H:%M:00', 'now')
    WHERE task_calendar_id = NEW.task_calendar_id;
END;

PRAGMA user_version = 1;