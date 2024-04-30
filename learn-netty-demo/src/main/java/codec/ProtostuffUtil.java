package codec;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;


@Slf4j
public class ProtostuffUtil {
	
	//存储因为无法直接序列化/反序列化 而需要被包装的类型Class
	private static final Set<Class<?>> WRAPPER_SET = new HashSet<Class<?>>();
	
	static {
		WRAPPER_SET.add(List.class);
		WRAPPER_SET.add(ArrayList.class);
		WRAPPER_SET.add(CopyOnWriteArrayList.class);
		WRAPPER_SET.add(LinkedList.class);
		WRAPPER_SET.add(Stack.class);
		WRAPPER_SET.add(Vector.class);
		WRAPPER_SET.add(Map.class);
		WRAPPER_SET.add(HashMap.class);
		WRAPPER_SET.add(TreeMap.class);
		WRAPPER_SET.add(LinkedHashMap.class);
		WRAPPER_SET.add(Hashtable.class);
		WRAPPER_SET.add(SortedMap.class);
		WRAPPER_SET.add(Object.class);
	}
	
	//注册需要使用包装类进行序列化的Class对象
	public static void registerWrapperClass(Class<?> clazz) {
		WRAPPER_SET.add(clazz);
	}
	
	/**
	 * 将对象序列化为字节数组
	 * @param t
	 * @param useWrapper 为true完全使用包装模式 为false则选择性的使用包装模式
	 * @param <T>
	 * @return
	 */
	public static <T> byte[] serialize(T t,boolean useWrapper) {
		Object serializerObj = t;
		if (useWrapper) {
			serializerObj = SerializeDeserializeWrapper.build(t);
		}
		return serialize(serializerObj);
	}
	
	/**
	 * 将对象序列化为字节数组
	 * @param t
	 * @param <T>
	 * @return
	 */
	public static <T> byte[] serialize(T t) {
		//获取序列化对象的class
		Class<T> clazz = (Class<T>) t.getClass();
		Object serializerObj = t;
		if (WRAPPER_SET.contains(clazz)) {
			serializerObj = SerializeDeserializeWrapper.build(t);//将原始序列化对象进行包装
		}
		return doSerialize(serializerObj);
	}
	
	
	/**
	 * 执行序列化
	 * @param t
	 * @param <T>
	 * @return
	 */
	public static <T> byte[] doSerialize(T t) {
		//获取序列化对象的class
		Class<T> clazz = (Class<T>) t.getClass();
		//获取Schema
		// RuntimeSchema<T> schema = RuntimeSchema.createFrom(clazz);//根据给定的class创建schema
		/**
		 * this is lazily created and cached by RuntimeSchema
		 * so its safe to call RuntimeSchema.getSchema() over and over The getSchema method is also thread-safe
		 */
		Schema<T> schema = RuntimeSchema.getSchema(clazz);//内部有缓存机制
		/**
		 * Re-use (manage) this buffer to avoid allocating on every serialization
		 */
		LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
		byte[] protostuff = null;
		try {
			protostuff = ProtostuffIOUtil.toByteArray(t, schema, buffer);
		} catch (Exception e){
			log.error("protostuff serialize error,{}",e.getMessage());
		}finally {
			buffer.clear();
		}
		return protostuff;
	}
	
	
	/**
	 * 反序列化
	 * @param data
	 * @param clazz
	 * @param <T>
	 * @return
	 */
	public static <T> T deserialize(byte[] data,Class<T> clazz) {
		//判断是否经过包装
		if (WRAPPER_SET.contains(clazz)) {
			SerializeDeserializeWrapper<T> wrapper = new SerializeDeserializeWrapper<T>();
			ProtostuffIOUtil.mergeFrom(data,wrapper,RuntimeSchema.getSchema(SerializeDeserializeWrapper.class));
			return wrapper.getData();
		}else {
			Schema<T> schema = RuntimeSchema.getSchema(clazz);
			T newMessage = schema.newMessage();
			ProtostuffIOUtil.mergeFrom(data,newMessage,schema);
			return newMessage;
		}
	}
	
	
	private static class SerializeDeserializeWrapper<T> {
		//被包装的数据
		T data;
		
		public static <T> SerializeDeserializeWrapper<T> build(T data){
			SerializeDeserializeWrapper<T> wrapper = new SerializeDeserializeWrapper<T>();
			wrapper.setData(data);
			return wrapper;
		}
		
		public T getData() {
			return data;
		}
		
		public void setData(T data) {
			this.data = data;
		}
	}
}
