package se.github.closebitcon.extra;


public abstract class ContainedThread
{
	private Thread thread = null;

	public boolean start()
	{
		if (thread != null)
			return false;
		thread = new Thread(new ThreadWorker());
		getThread().start();
		return true;
	}

	public boolean interrupt()
	{
		if (thread == null)
			return false;
		getThread().interrupt();
		return true;
	}

	public boolean isRunning()
	{
		return (thread != null);
	}

	public Thread getThread()
	{
		return thread;
	}

	private class ThreadWorker implements Runnable
	{
		@Override
		public void run()
		{
			try
			{
				ContainedThread.this.run();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			thread = null;
		}
	}

	protected abstract void run() throws Exception;
}
