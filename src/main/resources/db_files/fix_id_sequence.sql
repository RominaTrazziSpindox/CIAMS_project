DO $$
DECLARE
    record RECORD;
    max_id BIGINT;

BEGIN

    FOR record IN

        SELECT
        table_name,
        column_name,
        pg_get_serial_sequence(format('public.%I', table_name), column_name) AS sequence_name

        FROM information_schema.columns
        WHERE table_schema = 'public' AND column_default LIKE 'nextval%'

    LOOP
        -- Read the maximum id value of every table of the database
        EXECUTE format('SELECT MAX(%I) FROM public.%I', record.column_name, record.table_name)
        INTO max_id;

        -- Re-aligned the id sequence
        EXECUTE format( 'SELECT setval(%L, COALESCE(%s, 1), false)', record.sequence_name, max_id);

    END LOOP;

END $$;