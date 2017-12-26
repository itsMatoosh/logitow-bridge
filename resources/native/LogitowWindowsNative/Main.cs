using System;

namespace LogitowWindowsNative
{
    public class Program
    {
        /// <summary>
        /// The scanner being currently used.
        /// </summary>
        public static Scanner scanner;

        /// <summary>
        /// Entry point of the program.
        /// </summary>
        /// <param name="args"></param>
        public static void Main(string[] args)
        {
            scanner = new Scanner();
            scanner.StartBleDeviceWatcher();

            Console.WriteLine("Press to stop device discovery...");
            Console.ReadLine();
            scanner.StopBleDeviceWatcher();
        }
    }
}
