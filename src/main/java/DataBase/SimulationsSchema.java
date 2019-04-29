package DataBase;

public class SimulationsSchema
{
    //TODO java doc on constants
    public static final String TABLE_NAME = "SIMULATIONS";
    public class Columns
    {
        public static final String SIM_ID = "SIM_ID";
        public static final String NAME   = "NAME";
        public static final String TYPE = "TYPE";
    }

    public class Type_Column_Values
    {
        public static final String STATIC = "STATIC";
        public static final String DYNAMIC = "DYNAMIC";
    }
}
