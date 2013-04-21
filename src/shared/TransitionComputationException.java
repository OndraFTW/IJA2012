package shared;

public class TransitionComputationException extends Exception
{
	public String description;
	
	public TransitionComputationException(String d)
	{
		description=d;
	}
}
