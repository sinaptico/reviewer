package au.edu.usyd.reviewer.server.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;

public class CloneUtil {

	public static <E extends Object> E clone(E e) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, IntrospectionException, InstantiationException {
		if (!isPrimitiveOrWrapper(e)) {
			if (e instanceof Collection<?>) {
				return (E) cloneCollection((Collection<?>) e);
			} else if (!isPrimitiveOrWrapper(e)) {
				return cloneBean(e);
			}
		}
		return e;
	}

	protected static <E extends Object> E cloneBean(E e) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, IntrospectionException, InstantiationException {
		// clone bean
		E cloneE = (E) BeanUtils.cloneBean(e);

		// clone bean properties
		BeanInfo beanInfo = Introspector.getBeanInfo(e.getClass());
		for (PropertyDescriptor descriptor : beanInfo.getPropertyDescriptors()) {
			if (descriptor.getReadMethod() != null && descriptor.getWriteMethod() != null) {
				String name = descriptor.getName();
				Object value = PropertyUtils.getProperty(e, name);
				PropertyUtils.setProperty(cloneE, name, clone(value));
			}
		}
		return cloneE;
	}

	protected static <E extends Object, C extends Collection<E>> C cloneCollection(C c) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, IntrospectionException, InstantiationException {
		C cloneC;
		if (c instanceof List<?>) {
			cloneC = (C) new ArrayList<E>(c.size());
		} else if (c instanceof Set<?>) {
			cloneC = (C) new HashSet<E>(c.size());
		} else {
			throw new RuntimeException("Unsupported java.util.Collection implementation: " + c.getClass().getName());
		}
		for (E e : c) {
			cloneC.add(clone(e));
		}
		return cloneC;
	}

	protected static boolean isPrimitiveOrWrapper(Object object) {
		return !(object instanceof Object) || object instanceof Comparable<?>;
	}
}
