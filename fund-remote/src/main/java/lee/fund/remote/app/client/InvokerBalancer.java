package lee.fund.remote.app.client;

import lee.fund.remote.netty.client.Invoker;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/12/14 21:02
 * Desc:
 */
public interface InvokerBalancer {
    Invoker select(List<Invoker> invokers);

    static InvokerBalancer get(String mode){
        if ("RR".equals(mode)) {
            return new RoundRobinBalancer();
        }
        return new RandomBalancer();
    }

    class RoundRobinBalancer implements InvokerBalancer{
        private AtomicInteger cnt = new AtomicInteger();
        @Override
        public Invoker select(List<Invoker> invokers) {
            int index = Math.abs(cnt.incrementAndGet() % invokers.size());
            return invokers.get(index);
        }
    }

    class RandomBalancer implements InvokerBalancer{
        private Random random = new Random();
        @Override
        public Invoker select(List<Invoker> invokers) {
            int index = random.nextInt(invokers.size());
            return invokers.get(index);
        }
    }
}
