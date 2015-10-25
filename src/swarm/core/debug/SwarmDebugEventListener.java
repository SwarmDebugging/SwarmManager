package swarm.core.debug;

import java.util.Date;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.model.ISourceLocator;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.RuntimeProcess;
import org.eclipse.jdt.debug.core.IJavaStackFrame;
import org.eclipse.jdt.debug.core.IJavaThread;
import org.eclipse.jdt.internal.debug.core.model.JDIThread;

import swarm.core.domain.Event;
import swarm.core.domain.Invocation;
import swarm.core.domain.Method;
import swarm.core.domain.Session;
import swarm.core.domain.Type;
import swarm.core.services.InvocationService;
import swarm.core.services.TypeService;
import swarm.manager.views.StopSessionAction;

@SuppressWarnings("restriction")
public final class SwarmDebugEventListener implements IDebugEventSetListener {

	private Session session;
	private StopSessionAction stopAction;

	private Invocation lastInvocation;
	private boolean isStepInto = false;
	private boolean isBreakpoint = false;
	private Date now;
	private RuntimeProcess process;
	private IJavaStackFrame auxInvokingFrame;
	private IJavaStackFrame auxInvokedFrame;

	public SwarmDebugEventListener(Session session, StopSessionAction stopAction) {
		this.session = session;
		this.stopAction = stopAction;
		this.now = new Date();
	}

	@Override
	public void handleDebugEvents(DebugEvent[] events) {
		System.out.println("Event DebugListner " + now);

		for (DebugEvent debugEvent : events) {

			if (debugEvent.getKind() == DebugEvent.CREATE && !session.isActive()) {
				if (debugEvent.getSource() instanceof RuntimeProcess) {
					process = (RuntimeProcess) debugEvent.getSource();
					try {
						session.start();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				return;
			} else if (process != null && process.isTerminated()) {
				DebugPlugin.getDefault().removeDebugEventListener(this);
				stopAction.run();
				return;
			} else if (debugEvent.getDetail() == DebugEvent.STEP_INTO) {
				isStepInto = true;
				isBreakpoint = false;
			} else if (debugEvent.getDetail() == DebugEvent.BREAKPOINT) {
				isBreakpoint = true;
				isStepInto = false;
			} else {
				isBreakpoint = false;
				isStepInto = isStepInto && debugEvent.getKind() == DebugEvent.SUSPEND;
			}

			try {
				if (debugEvent.getSource() != null && debugEvent.getSource() instanceof JDIThread) {
					IJavaThread thread = (IJavaThread) debugEvent.getSource();
					ISourceLocator sourceLocator = thread.getLaunch().getSourceLocator();
					Type type;
					IFile iFile = getTypeFile(debugEvent, thread, sourceLocator);
					if (iFile != null) {
						type = findType(iFile.getFullPath().toString());
						if (type == null) {
							type = TypeService.createByPath(session, iFile.getFullPath().toString());
						}
					} else {
						return;
					}

					Event event = createEvent(type, debugEvent, thread, sourceLocator);

					IStackFrame[] frames = thread.getStackFrames();
					int stackLevels = frames.length;

					if (stackLevels > 1 && (isBreakpoint || isStepInto)) {
						if (isBreakpoint) {
							for (int i = frames.length - 1; i >= 1; i--) {
								IJavaStackFrame invokingFrame = (IJavaStackFrame) frames[i];
								IJavaStackFrame invokedFrame = (IJavaStackFrame) frames[i - 1];
								createInvocation(sourceLocator, event, invokingFrame, invokedFrame);
							}
							isBreakpoint = false;
						} else if (isStepInto) {
							IJavaStackFrame invokingFrame = (IJavaStackFrame) frames[1];
							IJavaStackFrame invokedFrame = (IJavaStackFrame) frames[0];

							if (!(invokingFrame.equals(auxInvokingFrame)
									&& invokedFrame.equals(auxInvokedFrame))) {
								createInvocation(sourceLocator, event, invokingFrame, invokedFrame);
								auxInvokingFrame = invokingFrame;
								auxInvokedFrame = invokedFrame;
							}
							isStepInto = false;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void createInvocation(ISourceLocator sourceLocator, Event event, IJavaStackFrame invokingFrame,
			IJavaStackFrame invokedFrame) throws DebugException, Exception {
		Object invokingElement = sourceLocator.getSourceElement(invokingFrame);
		Object invokedElement = sourceLocator.getSourceElement(invokedFrame);

		Method invokingMethod = null, invokedMethod = null;

		if (invokingElement instanceof IFile && invokedElement instanceof IFile) {
			invokingMethod = getMethodByElement(invokingFrame, invokingElement);
			invokedMethod = getMethodByElement(invokedFrame, invokedElement);

			Invocation invocation = new Invocation();
			invocation.setInvoking(invokingMethod);
			invocation.setInvoked(invokedMethod);
			invocation.setEvent(event);
			invocation.setSession(session);

			if ((isBreakpoint && !InvocationService.contains(invocation))
					|| (isStepInto && !invocation.equals(lastInvocation))) {
				invocation.create();
				lastInvocation = invocation;
			}
		}
	}

	private Method getMethodByElement(IJavaStackFrame invokingFrame, Object invokingElement) throws Exception {
		Method invokingMethod;
		IFile invokingFile = (IFile) invokingElement;
		Type typeInvoking = findType(invokingFile.getFullPath().toString());
		if (typeInvoking == null) {
			typeInvoking = TypeService.createByPath(session, invokingFile.getFullPath().toString());
		}
		invokingMethod = findMethod(invokingFrame, typeInvoking);

		return invokingMethod;
	}

	private IFile getTypeFile(DebugEvent debugEvent, IJavaThread thread, ISourceLocator sourceLocator)
			throws Exception {
		try {
			IStackFrame[] frames = thread.getStackFrames();

			if (frames.length > 0) {
				IJavaStackFrame invokingFrame = (IJavaStackFrame) frames[0];
				Object invokingElement = sourceLocator.getSourceElement(invokingFrame);

				if (invokingElement instanceof IFile) {
					return (IFile) invokingElement;
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private Event createEvent(Type type, DebugEvent debugEvent, IJavaThread thread, ISourceLocator sourceLocator) {
		try {
			IStackFrame[] frames = thread.getStackFrames();

			if (frames.length > 0) {
				IJavaStackFrame invokingFrame = (IJavaStackFrame) frames[0];
				Object invokingElement = sourceLocator.getSourceElement(invokingFrame);

				if (invokingElement instanceof IFile) {
					Method invokingMethod = findMethod(invokingFrame, type);
					if (invokingMethod == null) {
						return null;
					}

					Event event = new Event();
					event.setSession(session);
					event.setMethod(invokingMethod);
					event.setDetail(debugEvent.getDetail());
					event.setKind(debugEvent.getKind());
					event.setLineNumber(invokingFrame.getLineNumber());
					event.setCharStart(invokingFrame.getCharStart());
					event.setCharEnd(invokingFrame.getCharEnd());
					event.create();
					return event;
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private Method findMethod(IJavaStackFrame frame, Type type) throws Exception {
		Method method = null;
		String methodName = frame.getMethodName();

		List<Method> methods = type.getMethods();
		for (Method m : methods) {
			if (getKey(frame, type, methodName).equals(m.getKey())) {
				method = m;
				break;
			}
		}

		if (method == null) {
			method = new Method();
			method.setType(type);
			method.setName(methodName);
			method.setSignature(frame.getSignature().toString());
			String key = getKey(frame, type, methodName);
			method.setKey(key);
			method.create();
		}

		return method;
	}

	private String getKey(IJavaStackFrame frame, Type type, String methodName) throws DebugException {
		List<String> params = frame.getArgumentTypeNames();
		System.out.println("Get Key");
		for (String string : params) {
			System.out.println(string);
		}
		return "L" + type.getFullName() + ";." + methodName + frame.getSignature().toString();
	}

	private Type findType(String fullPath) {
		List<Type> types = session.getTypes();
		for (Type type : types) {
			if (type.getFullPath().equals(fullPath)) {
				return type;
			}
		}
		return null;
	}
}