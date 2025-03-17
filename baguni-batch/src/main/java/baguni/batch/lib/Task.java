package baguni.batch.lib;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * (1) 올바른 사용 예제
 * new Task().using("hello")                // 처리할 데이터
 *           .then(s1 -> s1.toUpperCase())  // HELLO
 *           .andThen(s2 -> s2 + " WORLD")  // HELLO WORLD
 *           .andThen(s3 -> s3 + "~!")      // HELLO WORLD~!
 *           .onFailure(e -> log.error(e))  // ------------
 *           .run();                        // 처리 시작
 *
 * (2) 잘못된 사용 예제
 * new Task().using("hello")
 *           .then(s -> s.toUpperCase())
 *           .andThen(s -> s + " WORLD")
 *           .run();   // 🔴 onFailure()를 명시하지 않으면 컴파일 오류 발생
 */
public class Task {

	public <T> DataSetupPipeline<T> using(T data) {
		return new DataSetupPipeline<>(data);
	}

	// 🟢 1. 초기 단계: then()만 가능
	public static class DataSetupPipeline<T> {
		private final T data;

		private DataSetupPipeline(T data) {
			this.data = data;
		}

		public ChainablePipeline<T> then(Function<T, T> function) {
			List<Function<T, T>> functions = new ArrayList<>();
			functions.add(function);
			return new ChainablePipeline<>(data, functions);
		}
	}

	// 🔵 2. 체이닝 단계: andThen() 또는 onFailure() 호출 가능
	public static class ChainablePipeline<T> {
		private final T data;
		private final List<Function<T, T>> functions;

		private ChainablePipeline(T data, List<Function<T, T>> functions) {
			this.data = data;
			this.functions = functions;
		}

		public ChainablePipeline<T> andThen(Function<T, T> function) {
			functions.add(function);
			return this;
		}

		public UnchainablePipeline<T> andThen(Consumer<T> endingFunction) {
			return new UnchainablePipeline<>(data, functions, endingFunction);
		}

		public RunnablePipeline<T> onFailure(Consumer<Exception> exceptionHandler) {
			return new RunnablePipeline<>(data, functions, null, exceptionHandler);
		}
	}

	// 🔵 3. 체이닝 불가능 단계: onFailure()만 호출 가능
	public static class UnchainablePipeline<T> {
		private final T data;
		private final List<Function<T, T>> functions;
		private final Consumer<T> endingFunction;

		private UnchainablePipeline(T data, List<Function<T, T>> functions, Consumer<T> endingFunction) {
			this.data = data;
			this.functions = functions;
			this.endingFunction = endingFunction;
		}

		public RunnablePipeline<T> onFailure(Consumer<Exception> exceptionHandler) {
			return new RunnablePipeline<>(data, functions, endingFunction, exceptionHandler);
		}
	}

	// 🔴 4. onFailure()를 통해 예외 처리가 설정된 안전한 Pipeline.
	//       runPipeline()으로 실행 가능
	public static class RunnablePipeline<T> implements Runnable {
		private T data;
		private final Consumer<T> endingFunction;
		private final List<Function<T, T>> functions;
		private final Consumer<Exception> failureHandler;

		private RunnablePipeline(
			T data, List<Function<T, T>> functions, Consumer<T> endingFunction, Consumer<Exception> failureHandler
		) {
			this.data = data;
			this.functions = functions;
			this.endingFunction = endingFunction;
			this.failureHandler = failureHandler;
		}

		@Override
		public void run() {
			try {
				for (Function<T, T> function : functions) {
					data = function.apply(data);
				}
				if (Objects.nonNull(endingFunction)) {
					endingFunction.accept(data);
				}
			} catch (Exception e) {
				failureHandler.accept(e);
			}
		}
	}
}
