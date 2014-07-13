import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by bulat.fattahov 2013
 */
public class PingPong {
    private ReentrantLock lock = new ReentrantLock();
    private volatile STATE last = STATE.NEW;
    private AtomicInteger pingCounter = new AtomicInteger();
    private AtomicInteger pongCounter = new AtomicInteger();

    void ping() {
        pingCounter.incrementAndGet();
        tryPing();
    }

    void pong() {
        pongCounter.incrementAndGet();
        tryPong();
    }

    private void tryPing() {
        if (lock.tryLock()) {
            try {
                if (!last.equals(STATE.PING) && pingCounter.get() > 0) {
                    last = STATE.PING;
                    report(last);
                    pingCounter.decrementAndGet();
                    tryPong();
                }
            } finally {
                lock.unlock();
            }
        }
    }

    private void tryPong() {
        if (lock.tryLock()) {
            try {
                if (!last.equals(STATE.PONG) && pongCounter.get() > 0) {
                    last = STATE.PONG;
                    report(last);
                    pongCounter.decrementAndGet();
                    tryPing();
                }
            } finally {
                lock.unlock();
            }
        }
    }

    void report(STATE state) {
        System.out.println(state);
    }

    @Override
    public String toString() {
        return "PingPong( PING = " + pingCounter.get() + ", PONG = " + pongCounter.get() + ")";
    }

    enum STATE {
        PING, PONG, NEW
    }
}
