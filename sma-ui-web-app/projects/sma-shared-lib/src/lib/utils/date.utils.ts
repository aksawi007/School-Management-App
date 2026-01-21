export class DateUtils {
  static formatDate(date: Date | string): string {
    if (typeof date === 'string') {
      date = new Date(date);
    }
    return date.toISOString().split('T')[0];
  }

  static parseDate(dateString: string): Date {
    return new Date(dateString);
  }

  static toDisplayFormat(date: Date | string): string {
    if (typeof date === 'string') {
      date = new Date(date);
    }
    return date.toLocaleDateString('en-IN', {
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    });
  }
}
