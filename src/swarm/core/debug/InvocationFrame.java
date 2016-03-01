package swarm.core.debug;

import org.eclipse.debug.core.model.ISourceLocator;
import org.eclipse.jdt.debug.core.IJavaStackFrame;

public class InvocationFrame {

	private ISourceLocator sourceLocator;
	private IJavaStackFrame invokingFrame;
	private IJavaStackFrame invokedFrame;

	public InvocationFrame(ISourceLocator sourceLocator, IJavaStackFrame invokingFrame, IJavaStackFrame invokedFrame) {
		this.sourceLocator = sourceLocator;
		this.invokingFrame = invokingFrame;
		this.invokedFrame = invokedFrame;
	}

	public ISourceLocator getSourceLocator() {
		return sourceLocator;
	}

	public void setSourceLocator(ISourceLocator sourceLocator) {
		this.sourceLocator = sourceLocator;
	}

	public IJavaStackFrame getInvokingFrame() {
		return invokingFrame;
	}

	public void setInvokingFrame(IJavaStackFrame invokingFrame) {
		this.invokingFrame = invokingFrame;
	}

	public IJavaStackFrame getInvokedFrame() {
		return invokedFrame;
	}

	public void setInvokedFrame(IJavaStackFrame invokedFrame) {
		this.invokedFrame = invokedFrame;
	}

}
