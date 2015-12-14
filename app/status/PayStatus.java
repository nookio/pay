package status;

/**
 * Created by nookio on 15/12/11.
 */
public enum PayStatus {

    /** 交易取消*/
    CANCEL{

        private Integer id = 0;

        private String name = "交易取消";

        @Override
        public Integer getId() {
            return id;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public boolean hasBefore() {
            return false;
        }

        @Override
        public PayStatus getBefore() {
            return null;
        }

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public PayStatus getNext() {
            return null;
        }
    },
    /** 等待支付*/
    PREPAY{

        private Integer id = 1;

        private String name = "等待支付";

        @Override
        public Integer getId() {
            return id;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public boolean hasBefore() {
            return false;
        }

        @Override
        public PayStatus getBefore() {
            return null;
        }

        @Override
        public boolean hasNext() {
            return true;
        }

        @Override
        public PayStatus getNext() {
            return PAYED;
        }
    },
    /** 支付成功*/
    PAYED{

        private Integer id = 2;

        private String name = "支付成功";

        @Override
        public Integer getId() {
            return id;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public boolean hasBefore() {
            return true;
        }

        @Override
        public PayStatus getBefore() {
            return PREPAY;
        }

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public PayStatus getNext() {
            return null;
        }
    },
    /** 交易失败*/
    FAIL{

        private Integer id = 3;

        private String name = "交易失败";

        @Override
        public Integer getId() {
            return id;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public boolean hasBefore() {
            return true;
        }

        @Override
        public PayStatus getBefore() {
            return PREPAY;
        }

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public PayStatus getNext() {
            return null;
        }
    };

    public abstract Integer getId();

    public abstract String getName();

    public abstract boolean hasBefore();

    public abstract PayStatus getBefore();

    public abstract boolean hasNext();

    public abstract PayStatus getNext();

    public int isBefore(PayStatus target) {
        if (getId() < target.getId()) {
            return 1;
        } else if (getId() == target.getId()) {
            return 0;
        }else {
            return -1;
        }
    }

    public PayStatus getByStatus(String status) {
        return valueOf(status);
    }
}
