import { Component, OnDestroy, OnInit } from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';
import { TimetableFile } from 'src/app/model/timetable-file';
import { TimetableFileService } from 'src/app/services/timetable-file.service';
import { TimetableService } from 'src/app/services/timetable.service';

@Component({
  selector: 'app-file-transfer',
  templateUrl: './file-transfer.component.html',
  styleUrls: ['./file-transfer.component.css']
})
export class FileTransferComponent implements OnInit, OnDestroy {
  public fileUpload: any = null;

  public selectedFile: any = null;
  public selectedFileName: string = '';
  public timetableFileNames: string[] = [];

  public uploadOkDiv: HTMLDivElement | null = null;
  public downloadOkDiv: HTMLDivElement | null = null;

  public timetableFileOkDiv: HTMLDivElement | null = null;

  constructor(
    private timetableFileService: TimetableFileService,
    private timetableService: TimetableService,
    private sanitizer: DomSanitizer) { }
  
  ngOnInit(): void {
    this.updateAllFileNames();

    this.uploadOkDiv = document.getElementById('successful-upload') as HTMLDivElement;
    this.downloadOkDiv = document.getElementById('successful-download') as HTMLDivElement;

    this.timetableFileOkDiv = document.getElementById('timetable-file-change') as HTMLDivElement;
  }

  ngOnDestroy(): void {
  }

  private updateAllFileNames(): void {
    this.timetableFileService.getAllFileNames()
    .subscribe((timetableFileNames: string[]) => {
      this.timetableFileNames = timetableFileNames;
      console.log(this.timetableFileNames);
    });
  }

  public onSelectUploadFile(event: any): void {
    this.fileUpload = event.target.files[0] ?? null;
    
    console.log(this.fileUpload);
    if (this.fileUpload == null) {
      return;
    }

    let file: File = this.fileUpload;
    let fileName: string = file.name;

    const formData: FormData = new FormData();
    formData.append('file', file, fileName);

    console.log(formData);

    this.timetableFileService.uploadFile(formData)
    .subscribe((response: any) => {
      console.log(response);
      setTimeout(() => {
        if (this.uploadOkDiv == null) {
          return;
        }

        this.uploadOkDiv.style.display = '';
      }, 5000);

      this.updateAllFileNames();
    });
  }

  public deleteFile(selectedFileName: string): void {
    this.timetableFileService.getTimetableFileIdByName(selectedFileName)
    .subscribe((id: number) => {
      let timetableFileId: number = id;
      this.timetableFileService.deleteFileById(timetableFileId)
      .subscribe(() => {
        console.log(`File with id ${timetableFileId} has been deleted.`);

        this.updateAllFileNames();
      });
    });
  }

  public useSelectedFile(): void {
    if (this.selectedFileName == '') {
      return;
    }

    this.timetableFileService.setFileAsDefault(this.selectedFileName)
    .subscribe((timetableFile: TimetableFile) => {
      console.log(`File ${timetableFile.name} has been set as default.`);
      window.location.reload();

      setTimeout(() => {
        if (this.timetableFileOkDiv == null) {
          return;
        }

        this.timetableFileOkDiv.style.display = '';
      }, 5000);
    });
  }

  public downloadCurrentConfiguration(): void {
    this.timetableService.downloadCurrentConfiguration()
    .subscribe((response: any) => {
      const blob = new Blob([response], { type: 'text/xml' });
      const url = window.URL.createObjectURL(blob);
      
      const link = document.createElement('a');
      link.href = url;
      link.download = 'timetable-export.xml';
      link.click();

      window.URL.revokeObjectURL(url);
        
      setTimeout(() => {
        if (this.downloadOkDiv == null) {
          return;
        }

        this.downloadOkDiv.style.display = '';
      }, 5000);
    });
  }
}
