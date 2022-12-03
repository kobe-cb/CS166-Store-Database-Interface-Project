DROP SEQUENCE IF EXISTS part_number_seq;
CREATE SEQUENCE part_number_seq START WITH 50000;

CREATE OR REPLACE LANGUAGE plpgsql;


-- Procedure 1: (Trigger 1) - 
CREATE OR REPLACE FUNCTION incValPro()
RETURNS trigger AS
$seq$
	BEGIN
		NEW.part_number := nextval('part_number_seq');
		RETURN NEW;
	END;
$seq$
LANGUAGE plpgsql VOLATILE;

-- Example Procedure
CREATE OR REPLACE FUNCTION update_order()
returns trigger AS
$ord$
	BEGIN
		insert into order_history values (nextvalf('part_number_seq'), now());
		return new;
		end;
$ord$
LANGUAGE plpgsql VOLATILE;

-- Example Trigger
drop trigger if exists update_trigger on customer;
create trigger update_trigger
after insert on customer
for each row
EXECUTE PROCEDURE update_order();


-- Trigger 1: (Procedure 1) - 
DROP TRIGGER IF EXISTS incVal ON part_nyc;
CREATE TRIGGER incVal BEFORE INSERT
ON part_nyc FOR EACH ROW
EXECUTE PROCEDURE incValPro();
